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
package io.blockv.core.client.manager

import io.blockv.core.model.GeoGroup
import io.blockv.core.model.Group
import io.blockv.core.util.Callable
import io.blockv.core.client.builder.DiscoverQueryBuilder
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
   * @return new Callable<Group> instance.
   * @see Group
   */
  fun getVatoms(vararg ids: String): Callable<Group>

  /**
   * Fetches the current users inventory.
   *
   * @param id is the id of the inventory you want to fetch.
   * @return new Callable<Group> instance.
   * @see Group
   */
  fun getInventory(id: String?): Callable<Group>

  /**
   * Fetches the dropped vAtoms in the specified area.
   *
   * @param left
   * @param bottom
   * @param right
   * @param top
   * @param limit
   * @param filter
   * @return new Callable<Group> instance.
   * @see GeoFilter
   * @see Group
   */
  fun geoDiscover(left: Double, bottom: Double, right: Double, top: Double, limit: Int, filter: GeoFilter): Callable<Group>

  /**
   * Fetches the count of vAtoms dropped in specified area.
   *
   * @param left
   * @param bottom
   * @param right
   * @param top
   * @param precision
   * @param filter
   * @return new Callable<List<GeoGroup> instance.
   * @see GeoFilter
   * @see GeoGroup
   */
  fun geoDiscoverGroup(left: Double, bottom: Double, right: Double, top: Double, precision: Int, filter: GeoFilter): Callable<List<GeoGroup>>

  /**
   * Updates the vAtom's properties.
   *
   * @param payload contains the properties to update.
   * @return new Callable<Void> instance.
   */
  fun updateVatom(payload: JSONObject): Callable<Void?>

  /**
   * Fetches all the actions configured for a template.
   *
   * @param templateId is the unique identified of the template.
   * @return new Callable<List<Action>> instance.
   * @see io.blockv.core.model.Action
   */
  fun getVatomActions(templateId: String): Callable<List<io.blockv.core.model.Action>>

  /**
   * Performs an action on the BLOCKv Platform.
   *
   * @param action is the name of the action to perform, e.g. "Drop".
   * @param id is the id of the vAtom to preform the action on.
   * @param payload contains the data required to do the action.
   * @return new Callable<JSONObject>.
   */
  fun preformAction(action: String, id: String, payload: JSONObject?): Callable<JSONObject?>

  /**
   * Performs an action on the BLOCKv Platform.
   *
   * @param action is the action to perform.
   * @param id is the id of the vAtom to preform the action on.
   * @param payload contains the data required to do the action.
   * @return new Callable<JSONObject>.
   */
  fun preformAction(action: Action, id: String, payload: JSONObject?): Callable<JSONObject?>

  /**
   * Performs an acquire action on a vAtom.
   *
   * Often, only a vAtom's ID is known, e.g. scanning a QR code with an embedded vAtom
   * ID. This call is useful is such circumstances.
   *
   * @param id is the identifier of the vAtom to acquire.
   * @return new Callable<JSONObject>.
   */
  fun acquireVatom(id: String): Callable<JSONObject?>

  /**
   * Attempts to transfer a vAtom to a user.
   *
   * @param id is the vAtom's id.
   * @param tokenType is the type of the user's token.
   * @param token is the user's token matching the provided type.
   * @return new Callable<JSONObject>.
   */
  fun transferVatom(id: String, tokenType: TokenType, token: String): Callable<JSONObject?>

  /**
   * Attempts to drop a vAtom on the map.
   *
   * @param id is the vAtom's id.
   * @param latitude
   * @param longitude
   * @return new Callable<JSONObject>.
   */
  fun dropVatom(id: String, latitude: Double, longitude: Double): Callable<JSONObject?>

  /**
   * Attempts to pick up a vAtom from the map.
   *
   * @param id is the vAtom's id.
   * @return new Callable<JSONObject>.
   */
  fun pickupVatom(id: String): Callable<JSONObject?>

  /**
   * Searches for vAtoms on the BLOCKv Platform.
   *
   * @param query is a JSONObject containing the discover query.
   * @return new Callable<Group>.
   * @see DiscoverQueryBuilder
   */
  fun discover(query: JSONObject): Callable<Group>

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