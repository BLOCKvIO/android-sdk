package io.blockv.rx.client.manager

import io.blockv.core.client.manager.VatomManager
import io.blockv.core.model.Inventory
import io.reactivex.Completable
import io.reactivex.Single
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by LordCheddar on 2018/02/25.
 */
interface VatomManager {

  fun getVatoms(ids:List<String>): Single<Inventory>

  fun getInventory(id:String?,pageToken:String?,pageAmount:Int?): Single<Inventory>

  fun geoDiscover(latitude:Double,longitude:Double,radius:Int,limit:Int): Single<Inventory>

  fun getVatomActions(template:String): Single<List<io.blockv.core.model.Action>>

  fun preformAction(action:String,id:String,payload: JSONObject?): Completable

  fun preformAction(action: VatomManager.Action, id:String, payload: JSONObject?): Completable

  fun acquireVatom(id:String): Completable

  fun acquirePubVatom(id:String): Completable

  fun transferVatomByEmail(id:String,email:String): Completable

  fun transferVatomByPhoneNumber(id:String,phoneNumber:String): Completable

  fun transferVatomById(id:String,userId:String): Completable

  fun dropVatom(id:String,latitude:Double,longitude:Double): Completable

  fun pickupVatom(id:String): Completable

}