package io.blockv.rx.client.manager

import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.request.VatomRequest
import io.blockv.core.model.Action
import io.blockv.core.model.Inventory
import io.reactivex.Completable
import io.reactivex.Single
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/27.
 */
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