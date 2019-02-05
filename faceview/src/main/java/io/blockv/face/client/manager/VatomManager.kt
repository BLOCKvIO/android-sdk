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

import io.blockv.common.model.Vatom
import io.reactivex.Single
import org.json.JSONObject

interface VatomManager {

  /**
   * Fetches vAtoms by id.
   *
   * @param ids is a list of vAtom id's in the current users inventory.
   * @return new Single<List<Vatom>> instance.
   * @see Vatom
   */
  fun getVatoms(vararg ids: String): Single<List<Vatom>>

  /**
   * Fetches the current user's inventory of vAtoms.
   *
   * @param id is the id of the inventory you want to fetch. If null or '.' supplied the
   *           user's root inventory will be returned.
   * @param page indicates which slice of the vAtom inventory is returned. If set as
   *             zero, the first page is returned.
   * @param limit defines the number of vAtoms per response page (up to 100). If omitted or set as
   *              zero, the max number is returned.
   * @return new Single<List<Vatom>> instance.
   * @see Vatom
   */
  fun getInventory(id: String?, page: Int, limit: Int): Single<List<Vatom>>

  /**
   * Performs an action on the BLOCKv Platform.
   *
   * @param action is the name of the action to perform, e.g. "Drop".
   * @param payload contains the data required to do the action.
   * @return new Single<JSONObject>.
   */
  fun preformAction(action: String, payload: JSONObject): Single<JSONObject>

}