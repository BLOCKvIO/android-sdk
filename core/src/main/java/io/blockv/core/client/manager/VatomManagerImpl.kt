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

import io.blockv.common.builder.DiscoverQueryBuilder
import io.blockv.common.internal.net.rest.api.VatomApi
import io.blockv.common.internal.net.rest.request.*
import io.blockv.common.model.*
import io.blockv.common.util.Callable
import io.blockv.common.util.JsonUtil
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

  override fun updateVatom(vatom: Vatom, update: StateUpdateEvent): Callable<Vatom> {

    return Callable.single {

      if (vatom.id != update.vatomId)
        throw Exception("vAtom id does not match state event's vAtom id")

      val modified = update.vatomProperties.optString("when_modified", vatom.whenModified)

      val privateProp = JSONObject((vatom.private ?: JSONObject()).toString())

      if (update.vatomProperties.has("private")) JsonUtil.merge(
        privateProp,
        update.vatomProperties.getJSONObject("private")
      )

      val props = VatomProperty(vatom.property)

      // update root
      if (update.vatomProperties.has("vAtom::vAtomType")) {
        val properties = update.vatomProperties.getJSONObject("vAtom::vAtomType")

        if (properties.has("parent_id")) props.parentId = properties.getString("parent_id")
        if (properties.has("owner")) props.owner = properties.getString("owner")
        if (properties.has("notify_msg")) props.notifyMsg = properties.getString("notify_msg")
        if (properties.has("in_contract")) props.isInContract = properties.getBoolean("in_contract")
        if (properties.has("in_contract_with")) props.inContractWith = properties.getString("in_contract_with")
        if (properties.has("transferred_by")) props.transferredBy = properties.getString("transferred_by")
        if (properties.has("num_direct_clones")) props.numDirectClones = properties.getInt("num_direct_clones")
        if (properties.has("cloned_from")) props.clonedFrom = properties.getString("cloned_from")
        if (properties.has("cloning_score")) props.cloningScore = properties.getDouble("cloning_score").toFloat()
        if (properties.has("acquirable")) props.isAcquireable = properties.getBoolean("acquirable")
        if (properties.has("redeemable")) props.isRedeemable = properties.getBoolean("redeemable")
        if (properties.has("dropped")) props.isDropped = properties.getBoolean("dropped")
        if (properties.has("tradeable")) props.isTradeable = properties.getBoolean("tradeable")
        if (properties.has("transferable")) props.isTransferable = properties.getBoolean("transferable")

        if (properties.has("tags")) {
          val array = properties.getJSONArray("tags")

          val tags = ArrayList<String>()
          (0 until array.length()).forEach {
            tags.add(array.getString(it))
          }
          props.tags = tags
        }

        if (properties.has("visibility")) {
          val visibility = properties.getJSONObject("visibility")
          if (props.visibility == null) {
            props.visibility = VatomVisibility("", "")
          }
          if (visibility.has("type")) props.visibility?.type = visibility.getString("type")
          if (visibility.has("value")) props.visibility?.value = visibility.getString("value")
        }

        if (properties.has("geo_pos")) {
          val geoPos = properties.getJSONObject("geo_pos")
          if (geoPos.has("coordinates")) {
            val coord = geoPos.getJSONArray("coordinates")
            if (props.geoPos != null) {

              val list = ArrayList<Float>()
              (0 until coord.length()).forEach {
                list.add(coord.getDouble(it).toFloat())
              }

              props.geoPos?.coordinates = list
            }
          }
        }

        if (properties.has("commerce")
          && props.commerce != null
          && properties.getJSONObject("commerce").has("pricing")
          && props.commerce?.pricing != null
        ) {

          val pricing = properties
            .getJSONObject("commerce")
            .getJSONObject("pricing")

          if (pricing.has("pricingType")) {
            props.commerce?.pricing?.pricingType = pricing.getString("pricingType")
          }

          if (pricing.has("value")) {
            val value = pricing.getJSONObject("value")
            if (value.has("currency")) props.commerce?.pricing?.currency = value.getString("currency")
            if (value.has("price")) props.commerce?.pricing?.price = value.getString("price")
            if (value.has("valid_from")) props.commerce?.pricing?.validFrom = value.getString("valid_from")
            if (value.has("valid_through")) props.commerce?.pricing?.validThrough = value.getString("valid_through")
            if (value.has("vat_included")) props.commerce?.pricing?.isVatIncluded = value.getBoolean("vat_included")

          }
        }
      }
      val newVatom = Vatom(
        vatom.id,
        vatom.whenCreated,
        modified,
        props,
        privateProp,
        vatom.faces,
        vatom.actions
      )

      newVatom
    }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.MAIN)
  }

}