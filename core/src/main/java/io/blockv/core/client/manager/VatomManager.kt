package io.blockv.core.client.manager

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
  fun preformAction(action: String, id: String, payload: JSONObject?): Callable<Void?>

  /**
   * Performs an action
   * @param action is the action type
   * @param payload contains the data required to do the action
   */
  fun preformAction(action: Action, id: String, payload: JSONObject?): Callable<Void?>

  /**
   * Attempts to acquire a vatom
   *
   * @param id is the vatom's id
   */
  fun acquireVatom(id: String): Callable<Void?>

  /**
   * Attempts to transfer a vatom to a user
   *
   * @param id is the vatom's id
   * @param tokenType is the type of the user's token
   * @param token is the user's token matching the provided type
   */
  fun transferVatom(id: String, tokenType: TokenType, token: String): Callable<Void?>

  /**
   * Attempts to drop a vatom on the map
   *
   * @param id is the vatom's id
   * @param latitude
   * @param longitude
   */
  fun dropVatom(id: String, latitude: Double, longitude: Double): Callable<Void?>

  /**
   * Attempts to pick up a vatom from the map
   *
   * @param id is the vatom's id
   */
  fun pickupVatom(id: String): Callable<Void?>


  fun discover(query: JSONObject): Callable<Group>

  enum class TokenType {
    EMAIL,
    PHONE_NUMBER,
    ID
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