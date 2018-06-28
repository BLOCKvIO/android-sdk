package io.blockv.core.internal.json.deserializer.event

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.WebSocketEvent
import org.json.JSONObject

class WebsocketEventDeserializer : Deserializer<WebSocketEvent<JSONObject>> {
  override fun deserialize(data: JSONObject): WebSocketEvent<JSONObject>? {
    try {
      val messageType = data.getString("msg_type")
      val userId = data.optString("user_id", "")
      val payload = data.getJSONObject("payload")
      return WebSocketEvent<JSONObject>(messageType, userId, payload)
    } catch (e: Exception) {
      android.util.Log.e("WsDeserializer", e.message)
    }
    return null
  }
}