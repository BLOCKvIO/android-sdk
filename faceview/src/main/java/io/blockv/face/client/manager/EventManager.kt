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
package io.blockv.face.client.manager

import io.blockv.common.model.InventoryEvent
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.WebSocketEvent
import io.reactivex.Flowable

interface EventManager {

  /**
   *  Provides a stream of vAtom state update events.
   *
   *  @return Flowable<WebSocketEvent<StateUpdateEvent> instance.
   */
  fun getVatomStateEvents(): Flowable<WebSocketEvent<StateUpdateEvent>>

  /**
   *  Provides a stream of inventory update events. Receiving this event indicates
   *  that a vAtom has been either added or removed from the user's inventory.
   *
   *  @return Flowable<WebSocketEvent<InventoryEvent> instance
   */
  fun getInventoryEvents(): Flowable<WebSocketEvent<InventoryEvent>>

}