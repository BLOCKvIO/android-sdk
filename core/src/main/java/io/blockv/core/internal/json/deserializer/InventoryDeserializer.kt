package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.Action
import io.blockv.core.model.Face
import io.blockv.core.model.Inventory
import io.blockv.core.model.Vatom
import org.json.JSONArray
import org.json.JSONObject

class InventoryDeserializer(val vatomDeserializer: Deserializer<Vatom?>,
                            val faceDeserializer: Deserializer<Face?>,
                            val actionDeserializer: Deserializer<Action?>) : Deserializer<Inventory> {

  override fun deserialize(data: JSONObject): Inventory? {
    try {
      val inventory: JSONArray? = if (data.has("inventory")) data.optJSONArray("inventory") else data.optJSONArray("objects") //so it can work with get current user vatom payload

      val faces: JSONArray? = data.optJSONArray("faces")
      val actions: JSONArray? = data.optJSONArray("actions")

      var inventoryArray: ArrayList<Vatom> = ArrayList()
      val facesArray: ArrayList<Face> = ArrayList()
      val actionsArray: ArrayList<Action> = ArrayList()

      if (inventory != null) {
        (0..inventory.length())
          .forEach {
            val vatom: Vatom? = vatomDeserializer.deserialize(inventory.optJSONObject(it))
            if (vatom != null) {
              inventoryArray.add(vatom)
            }
          }
      }
      if (faces != null) {
        (0..faces.length())
          .forEach {
            val face: Face? = faceDeserializer.deserialize(faces.optJSONObject(it))
            if (face != null) {
              facesArray.add(face)
            }
          }
      }
      if (actions != null) {
        (0..actions.length())
          .forEach {
            val action: Action? = actionDeserializer.deserialize(actions.optJSONObject(it))
            if (action != null) {
              actionsArray.add(action)
            }
          }
      }
      return Inventory(inventoryArray, facesArray, actionsArray)
    } catch (e: Exception) {
      android.util.Log.w("InventoryDeserializer", e.message)
    }
    return null
  }

}