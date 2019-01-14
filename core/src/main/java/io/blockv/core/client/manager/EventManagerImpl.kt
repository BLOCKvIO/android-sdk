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
package io.blockv.core.client.manager

import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.websocket.Websocket
import io.blockv.common.internal.net.websocket.WebsocketImpl
import io.blockv.common.model.ActivityEvent
import io.blockv.common.model.InventoryEvent
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.WebSocketEvent
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class EventManagerImpl(
  val webSocket: WebsocketImpl,
  val jsonModule: JsonModule
) : EventManager {

  @Volatile
  private var eventFlowable: Flowable<WebSocketEvent<JSONObject>>? = null

  companion object {
    val NULL_STATE_EVENT = WebSocketEvent<StateUpdateEvent>("", "", null)
    val NULL_INVENTORY_EVENT = WebSocketEvent<InventoryEvent>("", "", null)
    val NULL_ACTIVITY_EVENT = WebSocketEvent<ActivityEvent>("", "", null)
  }

  @Synchronized
  override fun getEvents(): Flowable<WebSocketEvent<JSONObject>> {
    if (eventFlowable == null) {
      eventFlowable = connect().share()
    }
    return eventFlowable!!
  }

  override fun getVatomStateEvents(): Flowable<WebSocketEvent<StateUpdateEvent>> {
    return getEvents()
      .observeOn(Schedulers.computation())
      .filter { event -> event.type == WebSocketEvent.MessageType.STATE_UPDATE }
      .map { event ->
        var stateEvent = NULL_STATE_EVENT
        if (event.payload != null) {
          val payload = jsonModule.deserialize<StateUpdateEvent>(event.payload!!)
          if (payload != null) {
            stateEvent = WebSocketEvent(
              event.messageType,
              event.userId,
              payload
            )
          }
        }
        stateEvent
      }
      .filter { event -> event !== NULL_STATE_EVENT }
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun getInventoryEvents(): Flowable<WebSocketEvent<InventoryEvent>> {
    return getEvents()
      .observeOn(Schedulers.computation())
      .filter { event -> event.type == WebSocketEvent.MessageType.INVENTORY }
      .map { event ->
        var stateEvent = NULL_INVENTORY_EVENT
        if (event.payload != null) {
          val payload = jsonModule.deserialize<InventoryEvent>(event.payload!!)
          if (payload != null) {
            stateEvent = WebSocketEvent(
              event.messageType,
              event.userId,
              payload
            )
          }
        }
        stateEvent
      }
      .filter { event -> event !== NULL_INVENTORY_EVENT }
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun getActivityEvents(): Flowable<WebSocketEvent<ActivityEvent>> {
    return getEvents()
      .observeOn(Schedulers.computation())
      .filter { event -> event.type == WebSocketEvent.MessageType.ACTIVITY }
      .map { event ->
        var stateEvent = NULL_ACTIVITY_EVENT
        if (event.payload != null) {
          val payload = jsonModule.deserialize<ActivityEvent>(event.payload!!)
          if (payload != null) {
            stateEvent = WebSocketEvent(
              event.messageType,
              event.userId,
              payload
            )
          }
        }
        stateEvent
      }
      .filter { event -> event !== NULL_ACTIVITY_EVENT }
      .observeOn(AndroidSchedulers.mainThread())
  }

  private fun connect(): Flowable<WebSocketEvent<JSONObject>> {

    return Flowable.create<WebSocketEvent<JSONObject>>({ subscriber ->
      val listener: Websocket.WebSocketListener = object : Websocket.WebSocketListener {
        override fun onEvent(event: WebSocketEvent<JSONObject>) {
          if (!subscriber.isCancelled) {
            subscriber.onNext(event)
          }
        }

        override fun onError(throwable: Throwable) {
          if (!subscriber.isCancelled) {
            subscriber.onError(throwable)
            subscriber.onComplete()
          }
        }

      }
      subscriber.setCancellable { webSocket.disconnect() }
      webSocket.connect(listener)
    }, BackpressureStrategy.BUFFER)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

}