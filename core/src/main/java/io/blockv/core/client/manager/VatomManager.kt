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

import io.blockv.common.builder.DiscoverQueryBuilder
import io.blockv.common.model.GeoGroup
import io.blockv.common.model.Message
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.Vatom
import io.blockv.common.model.VatomUpdate
import io.reactivex.Flowable
import io.reactivex.Single
import org.json.JSONObject
import java.util.*

/**
 *  This interface contains the available BLOCKv vAtom functions.
 */
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
   * Fetches the current user's inventory of vAtoms.
   *
   * @param id is the id of the inventory you want to fetch. If null or '.' supplied the
   *           user's root inventory will be returned.
   * @return new Flowable<Message<Vatom>> instance.
   * @see Vatom
   */
  fun getInventory(id: String): Flowable<Message<Vatom>>

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
   * @return new Single<List<Vatom>> instance.
   * @see GeoFilter
   * @see Vatom
   */
  fun geoDiscover(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    filter: VatomManager.GeoFilter
  ): Single<List<Vatom>>

  fun geoDiscover(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double
  ): Flowable<Message<Vatom>>

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
   * Set one or more Vatoms' parent id.
   *
   * @param parentId is the value which will be used to update the Vatoms' parent id.
   * @param vatomIds is list of one or more Vatom id.
   * @return new Single<VatomUpdate> instance.
   * @see VatomUpdate
   */
  fun setParentId(parentId: String, vararg vatomIds: String): Single<VatomUpdate>

  /**
   * Fetches all the actions configured for a template.
   *
   * @param templateId is the unique identified of the template.
   * @return new Single<List<Action>> instance.
   * @see io.blockv.common.model.Action
   */
  fun getVatomActions(templateId: String): Single<List<io.blockv.common.model.Action>>

  /**
   * Performs an action on the BLOCKv Platform.
   *
   * @param action is the name of the action to perform, e.g. "Drop".
   * @param payload contains the data required to do the action.
   * @return new Single<JSONObject> instance.
   */
  fun preformAction(action: String, payload: JSONObject): Single<JSONObject>

  /**
   * Performs an action on the BLOCKv Platform.
   *
   * @param action is the action to perform.
   * @param payload contains the data required to do the action.
   * @return new Single<JSONObject> instance.
   */
  fun preformAction(
    action: Action,
    payload: JSONObject
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
   * @return new Single<JSONObject> instance.
   */
  fun transferVatom(
    id: String,
    tokenType: TokenType,
    token: String
  ): Single<JSONObject>

  /**
   * Attempts to clone a vAtom to a user.
   *
   * @param id is the vAtom's id.
   * @param tokenType is the type of the user's token.
   * @param token is the user's token matching the provided type.
   * @return new Single<JSONObject> instance.
   */
  fun cloneVatom(
    id: String,
    tokenType: TokenType,
    token: String
  ): Single<JSONObject>

  /**
   * Attempts to drop a vAtom on the GeoMap.
   *
   * @param id is the vAtom's id.
   * @param latitude
   * @param longitude
   * @return new Single<JSONObject> instance.
   */
  fun dropVatom(id: String, latitude: Double, longitude: Double): Single<JSONObject>

  /**
   * Attempts to pick up a vAtom from the GeoMap.
   *
   * @param id is the vAtom's id.
   * @return new Single<JSONObject> instance.
   */
  fun pickupVatom(id: String): Single<JSONObject>


  /**
   * Searches for vAtoms on the BLOCKv Platform.
   *
   * @param query is a JSONObject containing the discover query.
   * @return new Single<List<Vatom>.
   * @see DiscoverQueryBuilder
   */
  fun discover(query: JSONObject): Single<List<Vatom>>

  /**
   * Trashes the specified vAtom.
   *
   * This will remove the vAtom from the current user's inventory.
   *
   * @param id is the identifier of the vAtom.
   * @return new Single<JSONObject> instance.
   */
  fun trashVatom(id: String): Single<JSONObject>

  /**
   * Creates a new updated vAtom by merging properties from the state update
   * into the provided vAtom model.
   *
   * @param vatom is the vAtom model to be updated.
   * @param update is the state update to be merged into the vAtom.
   * @return new Single<Vatom>.
   */
  fun updateVatom(vatom: Vatom, update: StateUpdateEvent): Single<Vatom>


  enum class TokenType {
    EMAIL,
    PHONE_NUMBER,
    ID
  }

  enum class GeoFilter {
    ALL,
    VATOMS,
    AVATARS
  }

  enum class Action {

    ACQUIRE {
      override fun action(): String = "Acquire"
    },
    TRANSFER {
      override fun action(): String = "Transfer"
    },
    CLONE {
      override fun action(): String = "Clone"
    },
    DROP {
      override fun action(): String = "Drop"
    },
    PICKUP {
      override fun action(): String = "Pickup"
    };

    abstract fun action(): String

    companion object {

      private val MAP: MutableMap<String, Action> = HashMap()

      init {
        for (action in Action.values()) {
          MAP.put(action.action().toLowerCase(), action)
        }
      }

      fun from(action: String): Action? = MAP[action.toLowerCase()]
    }
  }
}