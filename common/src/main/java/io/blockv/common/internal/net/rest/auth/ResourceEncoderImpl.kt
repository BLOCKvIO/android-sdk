/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
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