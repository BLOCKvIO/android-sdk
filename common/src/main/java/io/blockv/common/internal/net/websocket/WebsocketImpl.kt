/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.common.internal.net.websocket

import android.net.Uri
import android.util.Log
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketExtension
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.auth.Authenticator
import io.blockv.common.internal.net.websocket.request.BaseRequest
import io.blockv.common.internal.repository.Preferences
import io.blockv.common.model.GenericSocketEvent
import io.blockv.common.model.WebSocketEvent
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class WebsocketImpl(
  val preferences: Preferences,
  val jsonModule: JsonModule,
  val authenticator: Authenticator
) : WebSocketAdapter(), Websocket {

  @set:Synchronized
  @get:Synchronized
  var webSocket: WebSocket? = null
  val webSocketFlowable = Flowable.create<WebSocketEvent<JSONObject>>({ emitter ->

    val listener = object : WebSocketAdapter() {

      override fun onTextMessage(websocket: WebSocket?, message: String?) {
        if (message != null) {
          if (!emitter.isCancelled) {
            try {
              val event: GenericSocketEvent = jsonModule.deserialize(JSONObject(message))
              emitter.onNext(event)
            } catch (exception: Exception) {
              Log.i("WebSocket", exception.message)
            }
          }
        }
      }

      override fun onError(websocket: WebSocket, cause: WebSocketException) {
        super.onError(websocket, cause)
        if (!emitter.isCancelled) {
          cause.printStackTrace()
          emitter.onError(
            BlockvWsException(
              BlockvWsException.Error.CONNECTION_DISCONNECTED, null
            )
          )
        }
        websocket.removeListener(this)
      }

      override fun onDisconnected(
        websocket: WebSocket,
        serverCloseFrame: WebSocketFrame,
        clientCloseFrame: WebSocketFrame,
        closedByServer: Boolean
      ) {
        if (!emitter.isCancelled) {
          emitter.onError(
            BlockvWsException(
              BlockvWsException.Error.CONNECTION_DISCONNECTED, null
            )
          )
        }
        websocket.removeListener(this)
      }
    }

    try {
      val webSocket = WebSocketFactory()
        .createSocket(getAddress())
        .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
        .addListener(listener)
        .connect()
      this.webSocket = webSocket
      emitter.onNext(WebSocketEvent("connected", null, null))
      emitter.setCancellable {
        webSocket.disconnect()
      }
    } catch (exception: Exception) {
      if (!emitter.isCancelled) {
        emitter.onError(
          BlockvWsException(
            BlockvWsException.Error.CONNECTION_FAILED,
            exception
          )
        )
      }
    }
  }, BackpressureStrategy.BUFFER)
    .subscribeOn(Schedulers.io())
    .doOnError { it.printStackTrace() }
    .doFinally {
      synchronized(this)
      {
        webSocket = null
      }
    }
    .share()

  override fun handleCallbackError(websocket: WebSocket, cause: Throwable) {
    cause.printStackTrace()
  }

  @Synchronized
  override fun connect(): Flowable<WebSocketEvent<JSONObject>> {
    return Flowable.create({ emitter ->
      synchronized(this)
      {
        if (webSocket != null) {
          emitter.onNext(WebSocketEvent("connected", null, null))
        }
        emitter.setDisposable(
          webSocketFlowable
            .subscribe({
              emitter.onNext(it)
            }, {
              emitter.onError(it)
            })
        )
      }
    }, BackpressureStrategy.BUFFER)
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

  override fun sendMessage(message: String): Completable {
    return Completable.fromCallable {
      Log.e("message", message)
      webSocket!!.sendText(message)
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun sendMessage(request: BaseRequest): Completable {
    return sendMessage(request.toJson().toString())
  }

  override fun connectAndMessage(request: BaseRequest): Flowable<WebSocketEvent<JSONObject>> {
    return connect()
      .flatMap {
        if (it.type == WebSocketEvent.MessageType.CONNECTED) {
          sendMessage(request)
            .andThen(Flowable.just(it))
        } else
          Flowable.just(it)
      }
  }

}