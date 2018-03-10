package io.blockv.core.client.manager

import android.net.Uri
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.model.AssetProvider

/**
 * Created by LordCheddar on 2018/03/09.
 */
class ResourceManagerImpl(private val preferences: Preferences) : ResourceManager {

  override val assetProviders: List<AssetProvider>?
    get() = preferences.assetProviders

  override fun encodeUrl(url: String?): String? {
    if (url != null) {
      assetProviders?.forEach {
        if (url.startsWith(it.uri)) {
          val descriptor: Map<String, String?> = it.descriptor
          val original = Uri.parse(url)
          val out = Uri.parse(url).buildUpon().clearQuery()
          for (key in descriptor.keys) {
            out.appendQueryParameter(key, descriptor.get(key))
          }

          for (param in original.queryParameterNames) {
            if (!descriptor.keys.contains(param)) {
              out.appendQueryParameter(param, original.getQueryParameter(param))
            }
          }
          return out.build().toString()

        }
      }

    }
    return url
  }
}