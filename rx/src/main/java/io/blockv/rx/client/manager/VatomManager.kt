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

import io.blockv.core.client.builder.DiscoverQueryBuilder
import io.blockv.core.client.manager.VatomManager
import io.blockv.core.client.manager.VatomManager.GeoFilter
import io.blockv.core.model.Action
import io.blockv.core.model.DiscoverGroup
import io.blockv.core.model.GeoGroup
import io.blockv.core.model.Group
import io.reactivex.Completable
import io.reactivex.Single
import org.json.JSONObject

/**
 *  This interface contains the available BLOCKv vAtom functions.
 */
interface VatomManager {

  /**
   * Fetches vAtoms by id.
   *
   * @param ids is a list of vAtom id's in the current users inventory.
   * @return new Single<Group> instance.
   * @see Group
   */
  fun getVatoms(vararg ids: String): Single<Group>

  /**
   * Fetches the current users inventory.
   *
   * @param id is the id of the inventory you want to fetch.
   * @return new Single<Group> instance.
   * @see Group
   */
  fun getInventory(id: String?): Single<Group>

  /**
   * Performs a geo-search for vAtoms on the BLOCKv platform (i.e. vAtoms that have been
   * dropped by the vAtom owners).
   *
   * You must supply two coordinates (bottom-left and top-right) which from a rectangle.
   * This rectangle defines the geo search region.
   *
   * @param bottomLeftLat is the bottom left latitude coordinate.
   * @param bottomLeftLon is the bottom left longitude coordinate.
   * @param topRightLat is the top right latitude coordinate.
   * @param topRightLon is the top right longitude coordinate.
   * @param filter is the vAtom filter option to apply. Defaults to "vatoms".
   * @return new Single<Group> instance.
   * @see GeoFilter
   * @see Group
   */
  fun geoDiscover(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    filter: VatomManager.GeoFilter
  ): Single<Group>

  /**
   * Fetches the count of vAtoms dropped in the specified area.
   *
   * @param bottomLeftLat is the bottom left latitude coordinate.
   * @param bottomLeftLon is the bottom left longitude coordinate.
   * @param topRightLat is the top right latitude coordinate.
   * @param topRightLon is the top right longitude coordinate.
   * @param precision controls the density of the group distribution. Defaults to 3.
   *                  Lower values return fewer groups (with a higher vatom count) — less dense.
   *                  Higher values return more groups (with a lower vatom count) - more dense.
   * @param filter is the vAtom filter option to apply. Defaults to "vatoms".
   * @return new Single<List<GeoGroup> instance.
   * @see GeoFilter
   * @see GeoGroup
   */
  fun geoDiscoverGroups(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    precision: Int,
    filter: VatomManager.GeoFilter
  ): Single<List<GeoGroup>>

  /**
   * Updates the vAtom's properties.
   *
   * @param payload contains the properties to update.
   * @return new Completable instance.
   */
  fun updateVatom(payload: JSONObject): Completable

  /**
   * Fetches all the actions configured for a template.
   *
   * @param templateId is the unique identified of the template.
   * @return new Single<List<Action>> instance.
   * @see Action
   */
  fun getVatomActions(templateId: String): Single<List<Action>>

  /**
   * Performs an action on the BLOCKv Platform.
   *
   * @param action is the name of the action to perform, e.g. "Drop".
   * @param id is the id of the vAtom to preform the action on.
   * @param payload contains the data required to do the action.
   * @return new Single<JSONObject> instance.
   */
  fun preformAction(action: String, id: String, payload: JSONObject?): Single<JSONObject>

  /**
   * Performs an action on the BLOCKv Platform.
   *
   * @param action is the action to perform.
   * @param id is the id of the vAtom to preform the action on.
   * @param payload contains the data required to do the action.
   * @return new Single<JSONObject> instance.
   */
  fun preformAction(
    action: io.blockv.core.client.manager.VatomManager.Action,
    id: String,
    payload: JSONObject?
  ): Single<JSONObject>

  /**
   * Performs an acquire action on a vAtom.
   *
   * Often, only a vAtom's ID is known, e.g. scanning a QR code with an embedded vAtom.
   * ID. This call is useful is such circumstances.
   *
   * @param id is the identifier of the vAtom to acquire.
   * @return new Single<JSONObject> instance.
   */
  fun acquireVatom(id: String): Single<JSONObject>

  /**
   * Attempts to transfer a vAtom to a user.
   *
   * @param id is the vAtom's id.
   * @param tokenType is the type of the user's token.
   * @param token is the user's token matching the provided type.
   * @return new Completable instance.
   */
  fun transferVatom(
    id: String,
    tokenType: io.blockv.core.client.manager.VatomManager.TokenType,
    token: String
  ): Completable

  /**
   * Attempts to drop a vAtom on the map.
   *
   * @param id is the vAtom's id.
   * @param latitude
   * @param longitude
   * @return new Completable instance.
   */
  fun dropVatom(id: String, latitude: Double, longitude: Double): Completable

  /**
   * Attempts to pick up a vAtom from the map.
   *
   * @param id is the vAtom's id.
   * @return new Completable instance.
   */
  fun pickupVatom(id: String): Completable


  /**
   * Searches for vAtoms on the BLOCKv Platform.
   *
   * @param query is a JSONObject containing the discover query.
   * @return new Single<DiscoverGroup>.
   * @see DiscoverQueryBuilder
   * @see DiscoverGroup
   */
  fun discover(query: JSONObject): Single<DiscoverGroup>

  fun deleteVatom(id: String): Completable
}