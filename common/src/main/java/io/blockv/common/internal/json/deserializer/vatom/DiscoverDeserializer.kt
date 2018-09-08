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
package io.blockv.common.internal.json.deserializer.vatom

import io.blockv.common.internal.json.deserializer.Deserializer
import io.blockv.common.model.Action
import io.blockv.common.model.DiscoverPack
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import org.json.JSONArray
import org.json.JSONObject

class DiscoverDeserializer(
  val inventoryDeserializer: Deserializer<List<Vatom>>
) : Deserializer<DiscoverPack> {

  override fun deserialize(data: JSONObject): DiscoverPack? {
    try {
      val count: Int = data.optInt("count")
      data.put("vatoms",data.optJSONArray("results"))
      return DiscoverPack(count, inventoryDeserializer.deserialize(data)?:ArrayList())
    } catch (e: Exception) {
      android.util.Log.e("DiscoverDeserializer", e.message)
    }
    return null
  }

}