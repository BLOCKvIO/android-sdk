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

import io.blockv.core.client.manager.VatomManager
import io.blockv.core.model.Inventory
import io.reactivex.Completable
import io.reactivex.Single
import org.json.JSONObject
import java.util.HashMap

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