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

import io.blockv.core.client.builder.DiscoverQueryBuilder
import io.blockv.common.internal.net.rest.api.VatomApi
import io.blockv.common.internal.net.rest.request.*
import io.blockv.common.model.Action
import io.blockv.common.model.GeoGroup
import io.blockv.common.model.Vatom
import io.blockv.common.util.Callable
import org.json.JSONArray
import org.json.JSONObject

class VatomManagerImpl(val api: VatomApi) : VatomManager {

  override fun geoDiscover(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    filter: VatomManager.GeoFilter
  ): Callable<List<Vatom>> = Callable.single {
    api.geoDiscover(
      GeoRequest(
        bottomLeftLon,
        bottomLeftLat,
        topRightLon,
        topRightLat,
        filter.name.toLowerCase()
      )
    ).payload
  }

  override fun geoDiscoverGroups(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    precision: Int,
    filter: VatomManager.GeoFilter
  ): Callable<List<GeoGroup>> = Callable.single {
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
  }

  override fun updateVatom(payload: JSONObject): Callable<Void?> = Callable.single {
    api.updateVatom(payload).payload
  }

  override fun discover(query: JSONObject): Callable<List<Vatom>> = Callable.single {
    query.put(
      "return",
      JSONObject()
        .put("type", DiscoverQueryBuilder.ResultType.PAYLOAD)
        .put("fields", JSONArray())
    )
    api.discover(query).payload.vatoms
  }

  override fun discoverCount(query: JSONObject): Callable<Int> = Callable.single {
    query.put(
      "return",
      JSONObject()
        .put("type", DiscoverQueryBuilder.ResultType.COUNT)
        .put("fields", JSONArray())
    )
    api.discover(query).payload.count
  }


  override fun getVatoms(vararg ids: String): Callable<List<Vatom>> = Callable.single {
    api.getUserVatom(VatomRequest(ids.toList())).payload
  }

  override fun getInventory(id: String?, page: Int, limit: Int): Callable<List<Vatom>> = Callable.single {
    api.getUserInventory(
      InventoryRequest(
        (if (id == null || id.isEmpty()) "." else id),
        page,
        limit
      )
    ).payload
  }

  override fun getVatomActions(templateId: String): Callable<List<Action>> = Callable.single {
    api.getVatomActions(templateId).payload
  }

  override fun preformAction(
    action: String,
    id: String,
    payload: JSONObject?
  ): Callable<JSONObject?> = Callable.single {
    api.preformAction(PerformActionRequest(action, id, payload)).payload ?: JSONObject()
  }

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

  override fun trashVatom(id: String): Callable<Void?> = Callable.single {
    api.trashVatom(TrashVatomRequest(id))
    null
  }
}