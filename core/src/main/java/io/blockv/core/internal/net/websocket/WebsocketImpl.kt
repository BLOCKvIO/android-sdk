package io.blockv.core.internal.net.websocket

import android.net.Uri
import android.util.Log
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.auth.Authenticator
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.model.event.WebSocketEvent
import org.json.JSONObject

class WebsocketImpl(val preferences: Preferences,
                    val jsonModule: JsonModule,
                    val authenticator: Authenticator) : WebSocketAdapter(), Websocket {

  private var listener: WebSocketAdapter? = null
  private var webSocket: com.neovisionaries.ws.client.WebSocket? = null

  @Synchronized
  override fun connect(listener: Websocket.WebSocketListener) {
    this.disconnect()
    this.listener = object : WebSocketAdapter() {

      override fun onTextMessage(websocket: WebSocket?, message: String?) {
        if (message != null) {
          try {
            val event: WebSocketEvent<JSONObject>? = jsonModule.websocketEventDeserializer.deserialize(JSONObject(message))
            if (event != null) {
              listener.onEvent(event)
            }
          } catch (exception: Exception) {
            Log.i("WebSocket", exception.message)
          }
        }
      }

      override fun onDisconnected(websocket: WebSocket,
                                  serverCloseFrame: WebSocketFrame,
                                  clientCloseFrame: WebSocketFrame,
                                  closedByServer: Boolean) {
        listener.onError(BlockvWsException(BlockvWsException.Error.CONNECTION_DISCONNECTED, null))
        websocket.removeListener(this)
      }
    }

    try {
      this.webSocket = WebSocketFactory()
        .createSocket(getAddress())
        .addListener(this.listener)
        .connect()
    } catch (exception: Exception) {
      listener.onError(BlockvWsException(
        BlockvWsException.Error.CONNECTION_FAILED,
        exception))
    }

  }

  @Synchronized
  override fun disconnect() {
    if (this.webSocket != null) {
      this.webSocket!!.disconnect()
      this.webSocket!!.removeListener(this.listener)
    }
    this.webSocket = null
    this.listener = null
  }

  @Throws(Exception::class)
  private fun getAddress(): String {
      val environment = preferences.environment ?: throw NullPointerException("Environment is not set!")
      val access = authenticator.refreshToken() ?: throw NullPointerException("Unable to acquire authorization")
      val original = Uri.parse(environment.wss)
      val out = Uri.parse(environment.wss).buildUpon().clearQuery()

      out.appendQueryParameter("app_id", environment.appId)
      out.appendQueryParameter("token", access.token)

      for (param in original.queryParameterNames) {
        if (param != "token" || param != "app_id") {
          out.appendQueryParameter(param, original.getQueryParameter(param))
        }
      }
      return out.build().toString()
  }

}