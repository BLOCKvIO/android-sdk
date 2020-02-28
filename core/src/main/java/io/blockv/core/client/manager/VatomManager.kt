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

import androidx.paging.PagedList
import io.blockv.common.builder.DiscoverQueryBuilder
import io.blockv.common.model.GeoGroup
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.Vatom
import io.blockv.common.model.VatomGroup
import io.blockv.common.model.VatomUpdate
import io.reactivex.Flowable
import io.reactivex.Single
import org.json.JSONObject
import java.util.*

/**
 *  This interface contains the available BLOCKv Vatom functions.
 */
interface VatomManager {

  /**
   * Fetches Vatoms by id.
   *
   * @param ids is a list of Vatom id's in the current users inventory.
   * @return new Single<List<Vatom>> instance.
   * @see Vatom
   */
  fun getVatoms(vararg ids: String): Single<List<Vatom>>

  /**
   * Fetches a Vatom by id.
   *
   * @param id is a Vatom identifier in the current users inventory.
   * @return new Flowable<Message<Vatom>> instance.
   * @see Vatom
   */
  fun getVatom(id: String): Flowable<Pair<CacheState, Vatom?>>

  /**
   * Fetches the current user's inventory of Vatoms.
   *
   * @param id is the id of the inventory you want to fetch. If null or '.' supplied the
   *           user's root inventory will be returned.
   * @param page indicates which slice of the Vatom inventory is returned. If set as
   *             zero, the first page is returned.
   * @param limit defines the number of Vatoms per response page (up to 100). If omitted or set as
   *              zero, the max number is returned.
   * @return new Single<List<Vatom>> instance.
   * @see Vatom
   */
  fun getInventory(id: String?, page: Int, limit: Int): Single<List<Vatom>>

  /**
   * Fetches the current user's inventory of Vatoms.
   *
   * @param id is the id of the inventory you want to fetch. If null or '.' supplied the
   *           user's root inventory will be returned.
   * @param invalidate indicates if the cache should be resynchronized.
   *
   * @return new Flowable<Message<Vatom>> instance.
   * @see Vatom
   */
  fun getInventory(
    id: String,
    orderBy: SortOrder = SortOrder.ADDED,
    category: String = "",
    filter: String = "",
    limit: Int = -1,
    group: Boolean = false,
    invalidate: Boolean = false,
    initialIndex: Int = 0
  ): Flowable<PagedList<VatomGroup>>

  fun getVatoms(
    ids: List<String>,
    orderBy: SortOrder = SortOrder.ADDED,
    filter: String = "",
    limit: Int = -1,
    group: Boolean = false,
    invalidate: Boolean = false,
    initialIndex: Int = 0
  ): Flowable<PagedList<VatomGroup>>

  enum class SortOrder {
    ADDED,
    NEWEST,
    OLDEST,
    A_TO_Z,
    Z_TO_A
  }

  fun getCacheState(): Flowable<CacheState>

  enum class CacheState {
    UNSTABLE,
    STABLE,
    DISPOSED
  }

  fun getCategories(): Single<List<String>>

  /**
   * Performs a geo-search for Vatoms on the BLOCKv platform (i.e. Vatoms that have been
   * dropped by the Vatom owners).
   *
   * You must supply two coordinates (bottom-left and top-right) which from a rectangle.
   * This rectangle defines the geo search region.
   *
   * @param bottomLeftLat is the bottom left latitude coordinate.
   * @param bottomLeftLon is the bottom left longitude coordinate.
   * @param topRightLat is the top right latitude coordinate.
   * @param topRightLon is the top right longitude coordinate.
   * @param filter is the Vatom filter option to apply. Defaults to "vatoms".
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
  ): Flowable<List<Vatom>>

  /**
   * Fetches the count of Vatoms dropped in the specified area.
   *
   * @param bottomLeftLat is the bottom left latitude coordinate.
   * @param bottomLeftLon is the bottom left longitude coordinate.
   * @param topRightLat is the top right latitude coordinate.
   * @param topRightLon is the top right longitude coordinate.
   * @param precision controls the density of the group distribution. Defaults to 3.
   *                  Lower values return fewer groups (with a higher vatom count) — less dense.
   *                  Higher values return more groups (with a lower vatom count) - more dense.
   * @param filter is the Vatom filter option to apply. Defaults to "vatoms".
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
   * @param parentId is the value which will be used to update the Vatom's parent id.
   * @param vatomId is the id of the Vatom to update.
   * @return new Single<VatomUpdate> instance.
   * @see VatomUpdate
   */
  fun setParentId(vatomId: String, parentId: String): Single<VatomUpdate>

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
  fun performAction(action: String, payload: JSONObject): Single<JSONObject>

  /**
   * Performs an action on the BLOCKv Platform.
   *
   * @param action is the action to perform.
   * @param payload contains the data required to do the action.
   * @return new Single<JSONObject> instance.
   */
  fun performAction(
    action: Action,
    payload: JSONObject
  ): Single<JSONObject>

  /**
   * Performs an acquire action on a Vatom.
   *
   * Often, only a Vatom's ID is known, e.g. scanning a QR code with an embedded Vatom.
   * ID. This call is useful is such circumstances.
   *
   * @param id is the identifier of the Vatom to acquire.
   * @return new Single<JSONObject> instance.
   */
  fun acquireVatom(id: String): Single<JSONObject>

  /**
   * Attempts to transfer a Vatom to a user.
   *
   * @param id is the Vatom's id.
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
   * Attempts to clone a Vatom to a user.
   *
   * @param id is the Vatom's id.
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
   * Attempts to drop a Vatom on the GeoMap.
   *
   * @param id is the Vatom's id.
   * @param latitude
   * @param longitude
   * @return new Single<JSONObject> instance.
   */
  fun dropVatom(id: String, latitude: Double, longitude: Double): Single<JSONObject>

  /**
   * Attempts to pick up a Vatom from the GeoMap.
   *
   * @param id is the Vatom's id.
   * @return new Single<JSONObject> instance.
   */
  fun pickupVatom(id: String): Single<JSONObject>


  /**
   * Searches for Vatoms on the BLOCKv Platform.
   *
   * @param query is a JSONObject containing the discover query.
   * @return new Single<List<Vatom>.
   * @see DiscoverQueryBuilder
   */
  fun discover(query: JSONObject): Single<List<Vatom>>

  /**
   * Trashes the specified Vatom.
   *
   * This will remove the Vatom from the current user's inventory.
   *
   * @param id is the identifier of the Vatom.
   * @return new Single<JSONObject> instance.
   */
  fun trashVatom(id: String): Single<JSONObject>

  /**
   * Creates a new updated Vatom by merging properties from the state update
   * into the provided Vatom model.
   *
   * @param vatom is the Vatom model to be updated.
   * @param update is the state update to be merged into the Vatom.
   * @return new Single<Vatom>.
   */
  fun updateVatom(vatom: Vatom, update: StateUpdateEvent): Single<Vatom>

  /**
   * Performs a redemption request on the specified vatom id. This will trigger an
   * RPC socket event to the client informing it of the redemption request.
   *
   * @param vatomId is vatom identifier for redemption.
   * @return new Single<JSONObject>.
   */
  fun requestRedeem(vatomId: String): Single<JSONObject>

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