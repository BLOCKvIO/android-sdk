package io.blockv.core.model.event

class WebSocketEvent<out T>(val messageType: String,
                            val userId: String,
                            val payload: T?) {

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
}