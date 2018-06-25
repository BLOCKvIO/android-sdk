package io.blockv.rx.client.manager

import io.blockv.core.model.ActivityEvent
import io.blockv.core.model.InventoryEvent
import io.blockv.core.model.StateEvent
import io.blockv.core.model.WebSocketEvent
import io.reactivex.Flowable
import org.json.JSONObject

interface EventManager {

  fun getEvents(): Flowable<WebSocketEvent<JSONObject>>

  fun getVatomStateEvents(): Flowable<WebSocketEvent<StateEvent>>

  fun getInventoryEvents(): Flowable<WebSocketEvent<InventoryEvent>>

  fun getActivityEvents(): Flowable<WebSocketEvent<ActivityEvent>>
}