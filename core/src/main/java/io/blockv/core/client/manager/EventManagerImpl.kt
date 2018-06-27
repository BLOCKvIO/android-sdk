package io.blockv.core.client.manager

import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.websocket.Websocket
import io.blockv.core.internal.net.websocket.WebsocketImpl
import io.blockv.core.model.ActivityEvent
import io.blockv.core.model.InventoryEvent
import io.blockv.core.model.StateEvent
import io.blockv.core.model.WebSocketEvent
import io.blockv.core.util.Callable
import io.blockv.core.util.Cancellable
import org.json.JSONObject
import java.io.IOException


class EventManagerImpl(private val webSocket: WebsocketImpl,
                       val jsonModule: JsonModule) : EventManager {

  companion object {
    val NULL_STATE_EVENT = WebSocketEvent<StateEvent>("", "", null)
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

  override fun getStateEvents(): Callable<WebSocketEvent<StateEvent>> {
    return getEvents()
      .filter { it.type == WebSocketEvent.MessageType.STATE_UPDATE }
      .returnOn(Callable.Scheduler.IO)
      .map {
        var event: WebSocketEvent<StateEvent> = NULL_STATE_EVENT
        if (it.payload != null) {
          val stateEvent = jsonModule.stateEventDeserializer.deserialize(it.payload)
          if (stateEvent != null) {
            event = WebSocketEvent(
              it.messageType,
              it.userId,
              stateEvent)
          }
        }
        event
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
      .returnOn(Callable.Scheduler.IO)
      .doFinally {
        webSocket.disconnect()
        synchronized(resultEmitters, {
          resultEmitters.clear()
        })
      }


  }
}