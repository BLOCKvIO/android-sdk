package io.blockv.core.internal.net.websocket

import io.blockv.core.model.WebSocketEvent
import org.json.JSONObject

interface Websocket {

  fun connect(listener: WebSocketListener)

  fun disconnect()

  interface WebSocketListener{

    fun onEvent(event: WebSocketEvent<JSONObject>)

    fun onError(throwable: Throwable)
  }
}