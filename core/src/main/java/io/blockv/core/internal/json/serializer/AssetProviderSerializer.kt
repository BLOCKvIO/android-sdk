/**
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.internal.json.serializer

import io.blockv.core.model.AssetProvider
import org.json.JSONObject

class AssetProviderSerializer : Serializer<AssetProvider?> {
  override fun serialize(data: AssetProvider?): JSONObject {
    val out: JSONObject = JSONObject()
    if (data != null) {
      out.put("name", data.name)
      out.put("type", data.type)
      out.put("uri", data.uri)

      val descriptor: JSONObject = JSONObject()
      for (key: String in data.descriptor.keys) {
        descriptor.put(key, data.descriptor.getValue(key))
      }
      out.put("descriptor", descriptor)
    }
    return out
  }
}