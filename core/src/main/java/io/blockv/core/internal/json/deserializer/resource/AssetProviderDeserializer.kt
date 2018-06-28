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
package io.blockv.core.internal.json.deserializer.resource

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.resource.AssetProvider
import org.json.JSONObject

class AssetProviderDeserialzier : Deserializer<AssetProvider> {
  override fun deserialize(data: JSONObject): AssetProvider? {
    try {
      val descriptorObject: JSONObject = data.getJSONObject("descriptor")
      val descriptor: HashMap<String, String> = HashMap()
      for (key: String in descriptorObject.keys()) {
        descriptor.put(key, descriptorObject.getString(key))
      }
      return AssetProvider(data.getString("name"), data.getString("uri"), data.getString("type"), descriptor)
    } catch (e: Exception) {
      android.util.Log.w("AssetProvDeserializer", e.message)
    }
    return null
  }
}