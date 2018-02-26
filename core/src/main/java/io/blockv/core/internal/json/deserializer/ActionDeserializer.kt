package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.Action
import org.json.JSONObject

class ActionDeserializer : Deserializer<Action> {
  override fun deserialize(data: JSONObject): Action? {
    try {
      val name: String = data.getString("name")
      val parts: List<String> = name.split("::Action::")
      return Action(parts[0], parts[1])
    } catch (e: Exception) {
      android.util.Log.w("ActionDeserializer", e.message)
    }
    return null
  }

}