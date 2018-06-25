package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.StateEvent
import org.json.JSONObject

class StateEventDeserializer : Deserializer<StateEvent> {
  override fun deserialize(data: JSONObject): StateEvent? {
    try {
      val eventId = data.getString("event_id")
      val operation = data.getString("op")
      val vatomId = data.getString("id")
      val state = data.getJSONObject("new_object")
      val whenModified = data.getString("when_modified")
      return StateEvent(
        eventId,
        operation,
        vatomId,
        state,
        whenModified)
    } catch (e: Exception) {
      android.util.Log.e("StateEventDeserializer", e.message)
    }
    return null
  }
}