package io.blockv.core.internal.json.deserializer.event

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.StateUpdateEvent
import org.json.JSONObject

class StateEventDeserializer : Deserializer<StateUpdateEvent> {
  override fun deserialize(data: JSONObject): StateUpdateEvent? {
    try {
      val eventId = data.getString("event_id")
      val operation = data.getString("op")
      val vatomId = data.getString("id")
      val state = data.getJSONObject("new_object")
      return StateUpdateEvent(
        eventId,
        operation,
        vatomId,
        state)
    } catch (e: Exception) {
      android.util.Log.e("StateEventDeserializer", e.message)
    }
    return null
  }
}