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
package io.blockv.core.internal.json.deserializer.vatom

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.Action
import io.blockv.core.model.DiscoverPack
import io.blockv.core.model.Face
import io.blockv.core.model.Vatom
import org.json.JSONArray
import org.json.JSONObject

class DiscoverDeserializer(
  val vatomDeserializer: Deserializer<Vatom?>,
  val faceDeserializer: Deserializer<Face?>,
  val actionDeserializer: Deserializer<Action?>
) : Deserializer<DiscoverPack> {

  override fun deserialize(data: JSONObject): DiscoverPack? {
    try {
      val count: Int = data.optInt("count")

      val inventory: JSONArray? = data.optJSONArray("results")

      val faces: JSONArray? = data.optJSONArray("faces")
      val actions: JSONArray? = data.optJSONArray("actions")

      val inventoryArray: ArrayList<Vatom> = ArrayList()
      val facesArray: ArrayList<Face> = ArrayList()
      val actionsArray: ArrayList<Action> = ArrayList()

      if (inventory != null) {
        (0 until inventory.length())
          .forEach {
            val vatom: Vatom? = vatomDeserializer.deserialize(inventory.optJSONObject(it))
            if (vatom != null) {
              inventoryArray.add(vatom)
            }
          }
      }
      if (faces != null) {
        (0 until faces.length())
          .forEach {
            val face: Face? = faceDeserializer.deserialize(faces.optJSONObject(it))
            if (face != null) {
              facesArray.add(face)
            }
          }
      }
      if (actions != null) {
        (0 until actions.length())
          .forEach {
            val action: Action? = actionDeserializer.deserialize(actions.optJSONObject(it))
            if (action != null) {
              actionsArray.add(action)
            }
          }
      }
      return DiscoverPack(count, inventoryArray, facesArray, actionsArray)
    } catch (e: Exception) {
      android.util.Log.e("DiscoverDeserializer", e.message)
    }
    return null
  }

}