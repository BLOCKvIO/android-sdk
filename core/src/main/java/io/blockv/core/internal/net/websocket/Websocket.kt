package io.blockv.core.internal.net.websocket

interface Websocket {

  fun connect(listener: WebsocketListener)

  fun disconnect()
}