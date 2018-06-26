package io.blockv.core.client.manager

import io.blockv.core.model.ActivityEvent
import io.blockv.core.model.InventoryEvent
import io.blockv.core.model.StateEvent
import io.blockv.core.model.WebSocketEvent
import io.blockv.core.util.Callable
import org.json.JSONObject


interface EventManager {

  fun getEvents(): Callable<WebSocketEvent<JSONObject>>

  fun getStateEvents(): Callable<WebSocketEvent<StateEvent>>

  fun getInventoryEvents(): Callable<WebSocketEvent<InventoryEvent>>

  fun getActivityEvents(): Callable<WebSocketEvent<ActivityEvent>>
}