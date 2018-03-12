package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.Action
import io.blockv.core.model.Face
import io.blockv.core.model.Group
import io.blockv.core.model.Vatom
import org.json.JSONArray
import org.json.JSONObject

class InventoryDeserializer(val vatomDeserializer: Deserializer<Vatom?>,
                            val faceDeserializer: Deserializer<Face?>,
                            val actionDeserializer: Deserializer<Action?>) : Deserializer<Group> {

  override fun deserialize(data: JSONObject): Group? {
    try {
      val inventory: JSONArray? = data.optJSONArray("vatoms")

      val faces: JSONArray? = data.optJSONArray("faces")
      val actions: JSONArray? = data.optJSONArray("actions")

      var inventoryArray: ArrayList<Vatom> = ArrayList()
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
      return Group(inventoryArray, facesArray, actionsArray)
    } catch (e: Exception) {
      android.util.Log.e("InventoryDeserializer", e.message)
    }
    return null
  }

}