package io.blockv.core.internal.net.websocket

import io.blockv.core.model.WebSocketEvent
import org.json.JSONObject

interface WebsocketListener {

  fun onEvent(event: WebSocketEvent<JSONObject>)

  fun onError(throwable: Throwable)

  fun onDisconnect()
}