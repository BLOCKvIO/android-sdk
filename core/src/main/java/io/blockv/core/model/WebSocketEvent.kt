package io.blockv.core.model

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
          "inventory" -> MessageType.INVENTORY
          "state_update" -> MessageType.STATE_UPDATE
          "my_events" -> MessageType.ACTIVITY
          "info" -> MessageType.INFO
          else -> MessageType.UNKNOWN
        }
      }
    }
  }
}