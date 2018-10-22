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
import io.blockv.common.model.DiscoverPack
import io.blockv.common.model.Inventory
import org.json.JSONObject
import kotlin.reflect.KClass

class DiscoverDeserializer : Deserializer<DiscoverPack>() {

  override fun deserialize(
    type: KClass<*>,
    data: JSONObject,
    deserializers: Map<KClass<*>, Deserializer<*>>
  ): DiscoverPack? {
    try {
      val count: Int = data.optInt("count")
      data.put("vatoms", data.optJSONArray("results"))
      val inventory = deserializers[Inventory::class]?.deserialize(type, data, deserializers)

      return DiscoverPack(count, if (inventory != null) inventory as Inventory else ArrayList())
    } catch (e: Exception) {
      android.util.Log.e("DiscoverDeserializer", e.message)
    }
    return null
  }

}