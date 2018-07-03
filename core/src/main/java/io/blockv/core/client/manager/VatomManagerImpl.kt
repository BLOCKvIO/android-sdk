/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.client.manager

import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.internal.net.rest.request.*
import io.blockv.core.model.Action
import io.blockv.core.model.DiscoverGroup
import io.blockv.core.model.GeoGroup
import io.blockv.core.model.Group
import io.blockv.core.util.Callable
import org.json.JSONObject

class VatomManagerImpl(val api: VatomApi) : VatomManager {

  override fun geoDiscover(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    filter: VatomManager.GeoFilter
  ): Callable<Group> = Callable.single({
    api.geoDiscover(
      GeoRequest(
        bottomLeftLon,
        bottomLeftLat,
        topRightLon,
        topRightLat,
        filter.name.toLowerCase()
      )
    ).payload
  })

  override fun geoDiscoverGroups(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    precision: Int,
    filter: VatomManager.GeoFilter
  ): Callable<List<GeoGroup>> = Callable.single({
    assert(precision in 1..12) { "Precision required to be in the range  1 - 12" }
    api.geoGroupDiscover(
      GeoGroupRequest(
        bottomLeftLon,
        bottomLeftLat,
        topRightLon,
        topRightLat,
        precision,
        filter.name.toLowerCase()
      )
    ).payload
  })

  override fun updateVatom(payload: JSONObject): Callable<Void?> = Callable.single({
    api.updateVatom(payload).payload
  })

  override fun discover(query: JSONObject): Callable<DiscoverGroup> = Callable.single({
    api.discover(query).payload
  })

  override fun getVatoms(vararg ids: String): Callable<Group> = Callable.single({
    api.getUserVatom(VatomRequest(ids.toList())).payload
  })


  override fun getInventory(id: String?): Callable<Group> = Callable.single({
    api.getUserInventory(InventoryRequest((if (id == null || id.isEmpty()) "." else id))).payload
  })

  override fun getVatomActions(templateId: String): Callable<List<Action>> = Callable.single({
    api.getVatomActions(templateId).payload
  })

  override fun preformAction(
    action: String,
    id: String,
    payload: JSONObject?
  ): Callable<JSONObject?> = Callable.single({
    api.preformAction(PerformActionRequest(action, id, payload)).payload ?: JSONObject()
  })

  override fun preformAction(
    action: VatomManager.Action,
    id: String,
    payload: JSONObject?
  ): Callable<JSONObject?> = preformAction(action.action(), id, payload)

  override fun acquireVatom(id: String): Callable<JSONObject?> = preformAction(VatomManager.Action.ACQUIRE, id, null)


  override fun transferVatom(
    id: String,
    tokenType: VatomManager.TokenType,
    token: String
  ): Callable<JSONObject?> {
    val payload = JSONObject()
    when (tokenType) {
      VatomManager.TokenType.EMAIL -> payload.put("new.owner.email", token)
      VatomManager.TokenType.PHONE_NUMBER -> payload.put("new.owner.phone_number", token)
      VatomManager.TokenType.ID -> payload.put("new.owner.email", token)
    }
    return preformAction(VatomManager.Action.TRANSFER, id, payload)
  }

  override fun dropVatom(
    id: String,
    latitude: Double,
    longitude: Double
  ): Callable<JSONObject?> {
    val payload = JSONObject()
    payload.put(
      "geo.pos", JSONObject()
        .put("lat", latitude)
        .put("lon", longitude)
    )
    return preformAction(VatomManager.Action.DROP, id, payload)
  }

  override fun pickupVatom(id: String): Callable<JSONObject?> = preformAction(VatomManager.Action.PICKUP, id, null)

  override fun deleteVatom(id: String): Callable<Void?> = Callable.single {
    api.deleteVatom(DeleteVatomRequest(id))
    null
  }
}