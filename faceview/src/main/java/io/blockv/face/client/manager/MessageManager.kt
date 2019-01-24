package io.blockv.face.client.manager

import io.reactivex.Maybe
import org.json.JSONObject

interface MessageManager {

  fun sendMessage(message: Message): Maybe<Message>

  fun sendMessage(name: String, payload: JSONObject = JSONObject()): Maybe<Message>

  class Message(
    val name: String,
    val payload: JSONObject
  )

  enum class Error {
    UNSUPPORTED_MESSAGE,
    INVALID_PAYLOAD,
    OTHER
  }

  class MessageException(val error: Error, message: String) : Throwable(message)

}