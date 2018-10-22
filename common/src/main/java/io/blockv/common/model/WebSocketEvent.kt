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
package io.blockv.common.model

import org.json.JSONObject

class WebSocketEvent<T>(
  val messageType: String,
  val userId: String,
  val payload: T?
) {

  val type: MessageType
    get() = MessageType.from(messageType)

  enum class MessageType {
    INVENTORY,
    STATE_UPDATE,
    ACTIVITY,
    INFO,
    UNKNOWN;

    companion object {
      fun from(name: String): MessageType {
        return when (name) {
          "inventory" -> INVENTORY
          "state_update" -> STATE_UPDATE
          "my_events" -> ACTIVITY
          "info" -> INFO
          else -> UNKNOWN
        }
      }
    }
  }

  override fun toString(): String {
    return "WebSocketEvent{" +
      " messageType='" + messageType + '\'' +
      ", userId='" + userId + '\'' +
      ", payload='" + payload + '\'' +
      "}"
  }
}