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