package io.blockv.core.model.event

import org.json.JSONObject

class StateUpdateEvent(val eventId: String,
                       val operation: String,
                       val vatomId: String,
                       val vatomProperties: JSONObject) {

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