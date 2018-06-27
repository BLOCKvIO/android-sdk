package io.blockv.core.internal.net.websocket

import java.io.IOException

class BlockvWsException(val error: Error,
                        val internalException: Exception?) : IOException("Web Socket Error: " + error.name) {
  enum class Error {
    CONNECTION_FAILED,
    CONNECTION_DISCONNECT
  }
}