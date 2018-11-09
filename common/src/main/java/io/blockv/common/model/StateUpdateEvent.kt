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

import io.blockv.common.internal.json.serializer.Serializer
import org.json.JSONObject

class StateUpdateEvent {
  @Serializer.Serialize(name = "event_id")
  val eventId: String
  @Serializer.Serialize(name = "op")
  val operation: String
  @Serializer.Serialize(name = "id")
  val vatomId: String
  @Serializer.Serialize(name = "new_object")
  val vatomProperties: JSONObject

  @Serializer.Serializable
  constructor(
    eventId: String,
    operation: String,
    vatomId: String,
    vatomProperties: JSONObject
  ) {
    this.eventId = eventId
    this.operation = operation
    this.vatomId = vatomId
    this.vatomProperties = vatomProperties
  }


  override fun toString(): String {
    return "StateUpdateEvent{" +
      "eventId='" + eventId + '\'' +
      ", operation='" + operation + '\'' +
      ", vatomId='" + vatomId + '\'' +
      ", vatomProperties='" + vatomProperties.toString() + '\'' +
      "}"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is StateUpdateEvent) return false
    return eventId == other.eventId
  }

  override fun hashCode(): Int {
    return eventId.hashCode()
  }
}