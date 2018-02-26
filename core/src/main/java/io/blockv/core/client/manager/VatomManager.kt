package io.blockv.core.client.manager

import io.blockv.core.model.Inventory
import io.blockv.core.model.Vatom
import io.blockv.core.util.Observable
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by LordCheddar on 2018/02/25.
 */
interface VatomManager {

  fun getVatom(id:String): Observable<Vatom>

  fun getInventory(id:String): Observable<Inventory>

  fun geoDiscover(latitude:Double,longitude:Double,radius:Double): Observable<Inventory>

  fun getVatomActions(template:String): Observable<List<io.blockv.core.model.Action>>

  fun preformAction(action:String,id:String,payload: JSONObject?): Observable<Void?>

  fun preformAction(action: Action, id:String, payload: JSONObject?): Observable<Void?>

  fun acquireVatom(id:String): Observable<Void?>

  fun acquirePubVatom(id:String): Observable<Void?>

  fun transferVatomByEmail(id:String,email:String): Observable<Void?>

  fun transferVatomByPhoneNumber(id:String,phoneNumber:String): Observable<Void?>

  fun transferVatomById(id:String,userId:String): Observable<Void?>

  fun dropVatom(id:String,latitude:Double,longitude:Double): Observable<Void?>

  fun pickupVatom(id:String): Observable<Void?>


  enum class Action {

    ACQUIRE {
      override fun action(): String = "Acquire"
    },
    ACQUIRE_PUB_VARIATION {
      override fun action(): String = "AcquirePubVariation"
    },
    CLONE {
      override fun action(): String = "Clone"
    },
    TRANSFER {
      override fun action(): String = "Transfer"
    },
    DROP {
      override fun action(): String = "Drop"
    },
    PICKUP {
      override fun action(): String = "Pickup"
    },
    REDEEM {
      override fun action(): String = "Redeem"
    },
    DISCOVER {
      override fun action(): String = "Discover"
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