package io.blockv.core.client.manager

import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.internal.net.rest.request.GeoRequest
import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.request.PerformActionRequest
import io.blockv.core.internal.net.rest.request.VatomRequest
import io.blockv.core.model.Action
import io.blockv.core.model.Inventory
import io.blockv.core.util.Observable
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/26.
 */
class VatomManagerImpl(val api: VatomApi) : VatomManager {

  override fun getVatoms(ids: List<String>): Observable<Inventory> = object : Observable<Inventory>() {
    override fun getResult(): Inventory = api.getCurrentUserVatom(VatomRequest(ids)).payload ?: Inventory()
  }

  override fun getInventory(id: String?, pageToken: String?, pageAmount: Int?): Observable<Inventory> = object : Observable<Inventory>() {
    override fun getResult(): Inventory =
      api.getCurrentUserInventory(InventoryRequest((if (id == null || id.isEmpty()) "." else id), pageToken ?: "", pageAmount ?: 1000)).payload ?: Inventory()
  }

  override fun geoDiscover(latitude: Double, longitude: Double, radius: Int, limit: Int): Observable<Inventory> = object : Observable<Inventory>() {
    override fun getResult(): Inventory = api.geoDiscover(GeoRequest(latitude, longitude, radius, limit)).payload ?: Inventory()
  }

  override fun getVatomActions(template: String): Observable<List<Action>> = object : Observable<List<Action>>() {
    override fun getResult(): List<Action> = api.getVatomActions(template).payload
  }

  override fun preformAction(action: String, id: String, payload: JSONObject?): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {
      api.preformAction(PerformActionRequest(action, id, payload))
      return null
    }
  }

  override fun preformAction(action: VatomManager.Action, id: String, payload: JSONObject?): Observable<Void?> =
    preformAction(action.action(), id, payload)

  override fun acquireVatom(id: String): Observable<Void?> = preformAction(VatomManager.Action.ACQUIRE, id, null)

  override fun acquirePubVatom(id: String): Observable<Void?> = preformAction(VatomManager.Action.ACQUIRE_PUB_VARIATION, id, null)

  override fun transferVatomByEmail(id: String, email: String): Observable<Void?> {
    val payload = JSONObject()
    payload.put("new.owner.email", email)
    return preformAction(VatomManager.Action.TRANSFER, id, payload)
  }

  override fun transferVatomByPhoneNumber(id: String, phoneNumber: String): Observable<Void?> {
    val payload = JSONObject()
    payload.put("new.owner.phone_number", phoneNumber)
    return preformAction(VatomManager.Action.TRANSFER, id, payload)
  }

  override fun transferVatomById(id: String, userId: String): Observable<Void?> {
    val payload = JSONObject()
    payload.put("new.owner.id", userId)
    return preformAction(VatomManager.Action.TRANSFER, id, payload)
  }

  override fun dropVatom(id: String, latitude: Double, longitude: Double): Observable<Void?> {
    val payload = JSONObject()
    payload.put("geo.pos", JSONObject()
      .put("Lat", latitude)
      .put("Lon", longitude))
    return preformAction(VatomManager.Action.DROP, id, payload)
  }

  override fun pickupVatom(id: String): Observable<Void?> = preformAction(VatomManager.Action.PICKUP, id, null)
}