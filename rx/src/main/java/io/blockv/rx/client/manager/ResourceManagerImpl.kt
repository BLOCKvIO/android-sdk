package io.blockv.rx.client.manager

import android.net.Uri
import io.blockv.core.internal.net.rest.auth.Authenticator
import io.blockv.core.internal.net.rest.auth.JwtDecoder
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.model.AssetProvider
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class ResourceManagerImpl(
  private val preferences: Preferences,
  private val authenticator: Authenticator,
  private var jwtDecoder: JwtDecoder
) : ResourceManager {

  override val assetProviders: List<AssetProvider>?
    get() = preferences.assetProviders

  @Throws(io.blockv.core.client.manager.ResourceManager.MissingAssetProviderException::class)
  override fun encodeUrl(url: String): String {
    if (assetProviders == null || assetProviders?.size == 0) throw io.blockv.core.client.manager.ResourceManager.MissingAssetProviderException()
    for (provider: AssetProvider in assetProviders ?: ArrayList()) {
      if (url.startsWith(provider.uri)) {
        val descriptor: Map<String, String?> = provider.descriptor
        return encodeParams(url, descriptor)
      }
    }
    return url
  }

  private fun encodeParams(url: String, params: Map<String, String?>): String {
    val original = Uri.parse(url)
    val out = Uri.parse(url).buildUpon().clearQuery()
    for (key in params.keys) {
      out.appendQueryParameter(key, params[key])
    }

    for (param in original.queryParameterNames) {
      if (!params.keys.contains(param)) {
        out.appendQueryParameter(param, original.getQueryParameter(param))
      }
    }

    return out.build().toString()
  }


  override fun encodeWithCredentials(url: String): Single<String> {
    return Single.fromCallable {
      val cdn: String = preferences.environment?.cdn ?: ""
      var out = url
      if (cdn.isNotEmpty() && url.startsWith(cdn)) {
        val map: HashMap<String, String> = HashMap()
        var token = authenticator.getToken() ?: authenticator.refreshToken()
        ?: throw NullPointerException("Token is null, are you logged in?")
        val decodedToken = jwtDecoder.decode(token)

        if (Date().time - decodedToken.expiration.time < 60 * 2000) {
          token = authenticator.getToken() ?: authenticator.refreshToken()
            ?: throw NullPointerException("Token is null, are you logged in?")
        }
        map["jwt"] = token.token
        out = encodeParams(url, map)

      } else {
        if (assetProviders == null || assetProviders?.size == 0) throw io.blockv.core.client.manager.ResourceManager.MissingAssetProviderException()

        for (provider: AssetProvider in assetProviders ?: ArrayList()) {
          if (url.startsWith(provider.uri)) {
            val descriptor: Map<String, String?> = provider.descriptor
            out = encodeParams(url, descriptor)
            break
          }
        }
      }
      out
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }
}