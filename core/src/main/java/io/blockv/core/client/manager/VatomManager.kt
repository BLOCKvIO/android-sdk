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
import org.json.JSONObject
import java.util.*

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
  fun getVatoms(vararg ids: String): Callable<Group>

  /**
   * Fetches the current users inventory
   *
   * @param id is the id of the inventory you want to fetch
   * @return new Callable<Group> instance
   */
  fun getInventory(id: String?): Callable<Group>

  fun geoDiscover(left: Double, bottom: Double, right: Double, top: Double, limit: Int, filter: GeoFilter): Callable<Group>

  fun geoDiscoverGroup(left: Double, bottom: Double, right: Double, top: Double, precision: Int, filter: GeoFilter): Callable<List<GeoGroup>>

  fun updateVatom(payload: JSONObject): Callable<Void?>

  /**
   * Fetches List of actions
   * @param templateId is which the actions are associated to
   * @return new Callable<List<io.blockv.core.model.Action>> instance
   */
  fun getVatomActions(templateId: String): Callable<List<io.blockv.core.model.Action>>

  /**
   * Performs an action
   * @param action is the action's name
   * @param payload contains the data required to do the action
   */
  fun preformAction(action: String, id: String, payload: JSONObject?): Callable<JSONObject?>

  /**
   * Performs an action
   * @param action is the action type
   * @param payload contains the data required to do the action
   */
  fun preformAction(action: Action, id: String, payload: JSONObject?): Callable<JSONObject?>

  /**
   * Attempts to acquire a vatom
   *
   * @param id is the vatom's id
   */
  fun acquireVatom(id: String): Callable<JSONObject?>

  /**
   * Attempts to transfer a vatom to a user
   *
   * @param id is the vatom's id
   * @param tokenType is the type of the user's token
   * @param token is the user's token matching the provided type
   */
  fun transferVatom(id: String, tokenType: TokenType, token: String): Callable<JSONObject?>

  /**
   * Attempts to drop a vatom on the map
   *
   * @param id is the vatom's id
   * @param latitude
   * @param longitude
   */
  fun dropVatom(id: String, latitude: Double, longitude: Double): Callable<JSONObject?>

  /**
   * Attempts to pick up a vatom from the map
   *
   * @param id is the vatom's id
   */
  fun pickupVatom(id: String): Callable<JSONObject?>


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