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
import io.blockv.common.model.Face
import io.blockv.common.model.Inventory
import io.blockv.common.model.Vatom
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KClass

class InventoryDeserializer : Deserializer<Inventory>() {

  override fun deserialize(
    type: KClass<*>,
    data: JSONObject,
    deserializers: Map<KClass<*>, Deserializer<*>>
  ): Inventory {
    try {
      val inventory: JSONArray? = data.optJSONArray("vatoms")

      val facesArray: JSONArray? = data.optJSONArray("faces")
      val actionsArray: JSONArray? = data.optJSONArray("actions")

      val inventoryArray = Inventory()

      val faces: HashMap<String, ArrayList<Face>> = HashMap()
      val actions: HashMap<String, ArrayList<Action>> = HashMap()

      if (facesArray != null) {
        (0 until facesArray.length())
          .forEach {
            val face = deserializers[Face::class]?.deserialize(Face::class, facesArray.optJSONObject(it), deserializers)
            if (face != null) {
              if (!faces.containsKey((face as Face).templateId)) {
                faces[face.templateId] = ArrayList()
              }
              faces[face.templateId]?.add(face)
            }
          }
      }
      if (actionsArray != null) {
        (0 until actionsArray.length())
          .forEach {
            val action =
              deserializers[Action::class]?.deserialize(Action::class, actionsArray.optJSONObject(it), deserializers)
            if (action != null) {
              if (!actions.containsKey((action as Action).templateId)) {
                actions[action.templateId] = ArrayList()
              }
              actions[action.templateId]?.add(action)
            }
          }
      }
      if (inventory != null) {
        (0 until inventory.length())
          .forEach {
            val vatom =
              deserializers[Vatom::class]?.deserialize(Vatom::class, inventory.optJSONObject(it), deserializers)
            if (vatom != null && vatom is Vatom) {

              inventoryArray.add(
                Vatom(
                  vatom.id,
                  vatom.whenCreated,
                  vatom.whenModified,
                  vatom.property,
                  vatom.private,
                  faces[vatom.property.templateId] ?: ArrayList(),
                  actions[vatom.property.templateId] ?: ArrayList()
                )
              )
            }
          }
      }
      return inventoryArray
    } catch (e: Exception) {
      android.util.Log.e("InventoryDeserializer", e.message)
    }
    return Inventory()
  }

}