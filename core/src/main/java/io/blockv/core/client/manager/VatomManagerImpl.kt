package io.blockv.core.client.manager

import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.model.Action
import io.blockv.core.model.Inventory
import io.blockv.core.model.Vatom
import io.blockv.core.util.Observable
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/26.
 */
class VatomManagerImpl(val api: VatomApi):VatomManager {
  override fun getVatom(id: String): Observable<Vatom> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getInventory(id: String): Observable<Inventory> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun geoDiscover(latitude: Double, longitude: Double, radius: Double): Observable<Inventory> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getVatomActions(template: String): Observable<List<Action>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun preformAction(action: String, id: String, payload: JSONObject?): Observable<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun preformAction(action: VatomManager.Action, id: String, payload: JSONObject?): Observable<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun acquireVatom(id: String): Observable<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun acquirePubVatom(id: String): Observable<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun transferVatomByEmail(id: String, email: String): Observable<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun transferVatomByPhoneNumber(id: String, phoneNumber: String): Observable<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun transferVatomById(id: String, userId: String): Observable<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun dropVatom(id: String, latitude: Double, longitude: Double): Observable<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun pickupVatom(id: String): Observable<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}