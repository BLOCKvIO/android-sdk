package io.blockv.face.client

import io.blockv.common.model.InventoryEvent
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.WebSocketEvent
import io.blockv.common.util.Callable

interface EventManager {

  /**
   *  Provides a stream of vAtom state update events.
   *
   *  @return Callable<WebSocketEvent<StateUpdateEvent> instance.
   */
  fun getVatomStateEvents(): Callable<WebSocketEvent<StateUpdateEvent>>

  /**
   *  Provides a stream of inventory update events. Receiving this event indicates
   *  that a vAtom has been either added or removed from the user's inventory.
   *
   *  @return Callable<WebSocketEvent<InventoryEvent> instance
   */
  fun getInventoryEvents(): Callable<WebSocketEvent<InventoryEvent>>

}