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

import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.request.VatomRequest
import io.blockv.core.model.Action
import io.blockv.core.model.Inventory
import io.reactivex.Completable
import io.reactivex.Single
import org.json.JSONObject

class VatomManagerImpl(val api: VatomApi) : VatomManager {
  override fun getVatoms(ids: List<String>): Single<Inventory> =
    Single.fromCallable { api.getCurrentUserVatom(VatomRequest(ids)).payload ?: Inventory() }

  override fun getInventory(id: String?, pageToken: String?, pageAmount: Int?): Single<Inventory> =
    Single.fromCallable {
      api.getCurrentUserInventory(InventoryRequest(
        (if (id == null || id.isEmpty()) "." else id),
        pageToken ?: "",
        pageAmount ?: 1000)).payload ?: Inventory()
    }

  override fun geoDiscover(latitude: Double, longitude: Double, radius: Int, limit: Int): Single<Inventory> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getVatomActions(template: String): Single<List<Action>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun preformAction(action: String, id: String, payload: JSONObject?): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun preformAction(action: io.blockv.core.client.manager.VatomManager.Action, id: String, payload: JSONObject?): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun acquireVatom(id: String): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun acquirePubVatom(id: String): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun transferVatomByEmail(id: String, email: String): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun transferVatomByPhoneNumber(id: String, phoneNumber: String): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun transferVatomById(id: String, userId: String): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun dropVatom(id: String, latitude: Double, longitude: Double): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun pickupVatom(id: String): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}