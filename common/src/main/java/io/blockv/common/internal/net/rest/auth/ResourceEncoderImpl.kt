package io.blockv.common.internal.net.rest.auth

import android.net.Uri
import io.blockv.common.internal.repository.Preferences

class ResourceEncoderImpl(private val preferences: Preferences) : ResourceEncoder {

  override fun encodeUrl(url: String): String {
    if (preferences.assetProviders.isEmpty()) throw ResourceEncoder.MissingAssetProviderException()

    preferences.assetProviders.forEach {
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

    return url
  }
}