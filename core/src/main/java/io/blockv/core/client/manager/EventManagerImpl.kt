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

import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.websocket.Websocket
import io.blockv.core.internal.net.websocket.WebsocketImpl
import io.blockv.core.model.ActivityEvent
import io.blockv.core.model.InventoryEvent
import io.blockv.core.model.StateUpdateEvent
import io.blockv.core.model.WebSocketEvent
import io.blockv.core.util.Callable
import io.blockv.core.util.Cancellable
import org.json.JSONObject


class EventManagerImpl(private val webSocket: WebsocketImpl,
                       val jsonModule: JsonModule) : EventManager {

  companion object {
    val NULL_STATE_EVENT = WebSocketEvent<StateUpdateEvent>("", "", null)
    val NULL_INVENTORY_EVENT = WebSocketEvent<InventoryEvent>("", "", null)
    val NULL_ACTIVITY_EVENT = WebSocketEvent<ActivityEvent>("", "", null)
  }

  @Volatile
  private var cancellable: Cancellable? = null

  private val resultEmitters: HashSet<Callable.ResultEmitter<WebSocketEvent<JSONObject>>> = HashSet()

  override fun getEvents(): Callable<WebSocketEvent<JSONObject>> {
    return Callable.create<WebSocketEvent<JSONObject>>({
      synchronized(resultEmitters, {
        it.doOnCompletion {
          synchronized(resultEmitters, {
            resultEmitters.remove(it)
            if (resultEmitters.size == 0) {
              cancellable?.cancel()
            }
          })
        }
        resultEmitters.add(it)
        if (cancellable == null || cancellable!!.isComplete() || cancellable!!.isCanceled()) {
          cancellable = connect().call({
            val event = it
            synchronized(resultEmitters, {
              resultEmitters.forEach {
                it.onResult(event)
              }
            })
          }, {
            synchronized(resultEmitters, {
              val throwable = it
              resultEmitters.forEach {
                it.onError(throwable)
              }
            })
          })
        }
      })
    }).runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.MAIN)
  }

  override fun getVatomStateEvents(): Callable<WebSocketEvent<StateUpdateEvent>> {
    return getEvents()
      .filter { it.type == WebSocketEvent.MessageType.STATE_UPDATE }
      .returnOn(Callable.Scheduler.IO)
      .map {
        var updateEvent: WebSocketEvent<StateUpdateEvent> = NULL_STATE_EVENT
        if (it.payload != null) {
          val stateEvent = jsonModule.stateUpdateEventDeserializer.deserialize(it.payload)
          if (stateEvent != null) {
            updateEvent = WebSocketEvent(
              it.messageType,
              it.userId,
              stateEvent)
          }
        }
        updateEvent
      }
      .filter { it !== NULL_STATE_EVENT }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.MAIN)
  }

  override fun getInventoryEvents(): Callable<WebSocketEvent<InventoryEvent>> {
    return getEvents()
      .filter { it.type == WebSocketEvent.MessageType.INVENTORY }
      .returnOn(Callable.Scheduler.IO)
      .map {
        var event: WebSocketEvent<InventoryEvent> = NULL_INVENTORY_EVENT
        if (it.payload != null) {
          val inventoryEvent = jsonModule.inventoryEventDeserializer.deserialize(it.payload)
          if (inventoryEvent != null) {
            event = WebSocketEvent(
              it.messageType,
              it.userId,
              inventoryEvent)
          }
        }
        event
      }
      .filter { it !== NULL_INVENTORY_EVENT }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.MAIN)
  }

  override fun getActivityEvents(): Callable<WebSocketEvent<ActivityEvent>> {
    return getEvents()
      .filter { it.type == WebSocketEvent.MessageType.ACTIVITY }
      .returnOn(Callable.Scheduler.IO)
      .map {
        var event: WebSocketEvent<ActivityEvent> = NULL_ACTIVITY_EVENT
        if (it.payload != null) {
          val activityEvent = jsonModule.activityEventDeserializer.deserialize(it.payload)
          if (activityEvent != null) {
            event = WebSocketEvent(
              it.messageType,
              it.userId,
              activityEvent)
          }
        }
        event
      }
      .filter { it !== NULL_ACTIVITY_EVENT }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.MAIN)
  }

  private fun connect(): Callable<WebSocketEvent<JSONObject>> {

    return Callable.create<WebSocketEvent<JSONObject>>({

      val listener: Websocket.WebSocketListener = object : Websocket.WebSocketListener {

        override fun onEvent(event: WebSocketEvent<JSONObject>) {
          if (!it.isCanceled() && !it.isComplete()) {
            it.onResult(event)
          }
        }

        override fun onError(throwable: Throwable) {
          if (!it.isCanceled() && !it.isComplete()) {
            it.onError(throwable)
          }
        }
      }

      webSocket.connect(listener)
    }).runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.COMP)
      .doFinally {
        webSocket.disconnect()
        synchronized(resultEmitters, {
          resultEmitters.clear()
        })
      }


  }
}