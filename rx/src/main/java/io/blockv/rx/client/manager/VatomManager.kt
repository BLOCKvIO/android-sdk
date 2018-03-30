/**
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

import io.blockv.core.model.Action
import io.blockv.core.model.Group
import io.reactivex.Completable
import io.reactivex.Single
import org.json.JSONObject

/**
 *  This interface contains the available Blockv vatom functions
 */
interface VatomManager {

  /**
   * Fetches vAtoms by id
   *
   * @param ids is a list of vatom id's in the current users inventory
   * @return new Callable<Group> instance
   */
  fun getVatoms(vararg ids: String): Single<Group>

  /**
   * Fetches the current users inventory
   *
   * @param id is the id of the inventory you want to fetch
   * @return new Callable<Group> instance
   */
  fun getInventory(id: String?): Single<Group>

  /**
   * Fetches List of actions
   * @param templateId is which the actions are associated to
   * @return new Callable<List<io.blockv.core.model.Action>> instance
   */
  fun getVatomActions(templateId: String): Single<List<Action>>

  /**
   * Performs an action
   * @param action is the action's name
   * @param payload contains the data required to do the action
   */
  fun preformAction(action: String, id: String, payload: JSONObject?): Completable

  /**
   * Performs an action
   * @param action is the action type
   * @param payload contains the data required to do the action
   */
  fun preformAction(action: io.blockv.core.client.manager.VatomManager.Action, id: String, payload: JSONObject?): Completable

  /**
   * Attempts to acquire a vatom
   *
   * @param id is the vatom's id
   */
  fun acquireVatom(id: String): Completable

  /**
   * Attempts to transfer a vatom to a user
   *
   * @param id is the vatom's id
   * @param tokenType is the type of the user's token
   * @param token is the user's token matching the provided type
   */
  fun transferVatom(id: String, tokenType: io.blockv.core.client.manager.VatomManager.TokenType, token: String): Completable

  /**
   * Attempts to drop a vatom on the map
   *
   * @param id is the vatom's id
   * @param latitude
   * @param longitude
   */
  fun dropVatom(id: String, latitude: Double, longitude: Double): Completable

  /**
   * Attempts to pick up a vatom from the map
   *
   * @param id is the vatom's id
   */
  fun pickupVatom(id: String): Completable


  fun discover(query: JSONObject): Single<Group>

}