package io.blockv.core.client.manager

import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.request.PerformActionRequest
import io.blockv.core.internal.net.rest.request.VatomRequest
import io.blockv.core.model.Action
import io.blockv.core.model.Group
import io.blockv.core.util.Callable
import org.json.JSONObject

class VatomManagerImpl(val api: VatomApi,
                       val resourceManager: ResourceManager) : VatomManager {

  override fun discover(query: JSONObject): Callable<Group> = object : Callable<Group>() {
    override fun getResult(): Group {
      val group = api.discover(query).payload ?: Group(ArrayList(), ArrayList(), ArrayList())
      group.vatoms.forEach {
        it.property.resources?.forEach {
          it.url = resourceManager.encodeUrl(it.url) ?: it.url
        }
      }
      return group
    }
  }

  override fun getVatoms(vararg ids: String): Callable<Group> = object : Callable<Group>() {
    override fun getResult(): Group {
      val group = api.getUserVatom(VatomRequest(ids.toList())).payload ?: Group(ArrayList(), ArrayList(), ArrayList())
      group.vatoms.forEach {
        it.property.resources?.forEach {
          it.url = resourceManager.encodeUrl(it.url) ?: it.url
        }
      }
      return group
    }
  }

  override fun getInventory(id: String?): Callable<Group> = object : Callable<Group>() {
    override fun getResult(): Group {
      val group = api.getUserInventory(InventoryRequest((if (id == null || id.isEmpty()) "." else id))).payload


      if (group != null) {

        group.vatoms.forEach {
          it.property.resources.forEach {
            it.url = resourceManager.encodeUrl(it.url) ?: it.url
          }
        }
        return group
      }

      return Group(ArrayList(), ArrayList(), ArrayList())
    }
  }

  override fun getVatomActions(template: String): Callable<List<Action>> = object : Callable<List<Action>>() {
    override fun getResult(): List<Action> = api.getVatomActions(template).payload
  }

  override fun preformAction(action: String, id: String, payload: JSONObject?): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {
      api.preformAction(PerformActionRequest(action, id, payload))
      return null
    }
  }

  override fun preformAction(action: VatomManager.Action, id: String, payload: JSONObject?): Callable<Void?> =
    preformAction(action.action(), id, payload)

  override fun acquireVatom(id: String): Callable<Void?> = preformAction(VatomManager.Action.ACQUIRE, id, null)


  override fun transferVatom(id: String, tokenType: VatomManager.TokenType, token: String): Callable<Void?> {
    val payload = JSONObject()
    when (tokenType) {
      VatomManager.TokenType.EMAIL -> payload.put("new.owner.email", token)
      VatomManager.TokenType.PHONE_NUMBER -> payload.put("new.owner.phone_number", token)
      VatomManager.TokenType.ID -> payload.put("new.owner.email", token)
    }

    return preformAction(VatomManager.Action.TRANSFER, id, payload)
  }

  override fun dropVatom(id: String, latitude: Double, longitude: Double): Callable<Void?> {
    val payload = JSONObject()
    payload.put("geo.pos", JSONObject()
      .put("Lat", latitude)
      .put("Lon", longitude))
    return preformAction(VatomManager.Action.DROP, id, payload)
  }

  override fun pickupVatom(id: String): Callable<Void?> = preformAction(VatomManager.Action.PICKUP, id, null)
}