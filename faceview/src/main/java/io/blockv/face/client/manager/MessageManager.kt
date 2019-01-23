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
}