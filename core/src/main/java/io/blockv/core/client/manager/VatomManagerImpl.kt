package io.blockv.core.client.manager

import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.request.PerformActionRequest
import io.blockv.core.internal.net.rest.request.VatomRequest
import io.blockv.core.model.Action
import io.blockv.core.model.Group
import io.blockv.core.util.Observable
import org.json.JSONObject

class VatomManagerImpl(val api: VatomApi,
                       val resourceManager: ResourceManager) : VatomManager {

  override fun getVatoms(vararg ids: String): Observable<Group> = object : Observable<Group>() {
    override fun getResult(): Group {
      val group = api.getUserVatom(VatomRequest(ids.toList())).payload ?: Group(ArrayList(),ArrayList(),ArrayList())
      group.vatoms.forEach {
        it.property.resources?.forEach {
          it.url = resourceManager.encodeUrl(it.url) ?: it.url
        }
      }
      return group
    }
  }

  override fun getInventory(id: String?): Observable<Group> = object : Observable<Group>() {
    override fun getResult(): Group {
      val group = api.getUserInventory(InventoryRequest((if (id == null || id.isEmpty()) "." else id))).payload


      if(group!=null) {

        group.vatoms.forEach {
          it.property.resources.forEach {
            it.url = resourceManager.encodeUrl(it.url) ?: it.url
          }
        }
        return group
      }

      return Group(ArrayList(),ArrayList(),ArrayList())
    }
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


  override fun transferVatom(id: String, tokenType: VatomManager.TokenType, token: String): Observable<Void?> {
    val payload = JSONObject()
    when (tokenType) {
      VatomManager.TokenType.EMAIL -> payload.put("new.owner.email", token)
      VatomManager.TokenType.PHONE_NUMBER -> payload.put("new.owner.phone_number", token)
      VatomManager.TokenType.ID -> payload.put("new.owner.email", token)
    }

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