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
package io.blockv.rx.client.manager

import io.blockv.core.model.event.ActivityEvent
import io.blockv.core.model.event.InventoryEvent
import io.blockv.core.model.event.StateUpdateEvent
import io.blockv.core.model.event.WebSocketEvent
import io.reactivex.Flowable
import org.json.JSONObject

/**
 * This interface contains the available BLOCKv web socket event streams.
 */
interface EventManager {

  /**
   *  Provides a stream of raw web socket events.
   *
   *  @return Flowable<WebSocketEvent<JSONObject>> instance.
   */
  fun getEvents(): Flowable<WebSocketEvent<JSONObject>>

  /**
   *  Provides a stream of vAtom state update events.
   *
   *  @return Flowable<WebSocketEvent<StateUpdateEvent>> instance.
   */
  fun getVatomStateEvents(): Flowable<WebSocketEvent<StateUpdateEvent>>

  /**
   *  Provides a stream of inventory update events. This only indicates if a vAtom
   *  has been added or removed from a specific inventory
   *
   *  @return Flowable<WebSocketEvent<InventoryEvent>> instance
   */
  fun getInventoryEvents(): Flowable<WebSocketEvent<InventoryEvent>>

  /**
   * Provides a stream of activity events
   *
   * @return Flowable<WebSocketEvent<ActivityEvent>> instance
   */
  fun getActivityEvents(): Flowable<WebSocketEvent<ActivityEvent>>
}