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
import io.blockv.common.internal.net.rest.request.GeoGroupRequest
import io.blockv.common.internal.net.rest.request.GeoRequest
import io.blockv.common.internal.net.rest.request.InventoryRequest
import io.blockv.common.internal.net.rest.request.PerformActionRequest
import io.blockv.common.internal.net.rest.request.TrashVatomRequest
import io.blockv.common.internal.net.rest.request.VatomRequest
import io.blockv.common.model.GeoGroup
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.Vatom
import io.blockv.common.model.VatomProperty
import io.blockv.common.model.VatomVisibility
import io.blockv.common.util.JsonUtil
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

class VatomManagerImpl(val api: VatomApi) : VatomManager {

  override fun geoDiscover(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    filter: VatomManager.GeoFilter
  ): Single<List<Vatom>> = Single.fromCallable {
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
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun geoDiscoverGroups(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    precision: Int,
    filter: VatomManager.GeoFilter
  ): Single<List<GeoGroup>> = Single.fromCallable {
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
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun updateVatom(payload: JSONObject): Completable = Completable.fromCallable {
    api.updateVatom(payload)
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getVatoms(vararg ids: String): Single<List<Vatom>> = Single.fromCallable {
    api.getUserVatom(VatomRequest(ids.toList())).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getInventory(id: String?, page: Int, limit: Int): Single<List<Vatom>> = Single.fromCallable {
    api.getUserInventory(
      InventoryRequest(
        (if (id == null || id.isEmpty()) "." else id),
        page,
        limit
      )
    ).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getVatomActions(templateId: String): Single<List<io.blockv.common.model.Action>> = Single.fromCallable {
    api.getVatomActions(templateId).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun preformAction(
    action: String,
    payload: JSONObject
  ): Single<JSONObject> = Single.fromCallable {
    api.preformAction(PerformActionRequest(action, payload)).payload ?: JSONObject()
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun preformAction(
    action: VatomManager.Action,
    payload: JSONObject
  ): Single<JSONObject> = preformAction(action.action(), payload)

  override fun acquireVatom(id: String): Single<JSONObject> =
    preformAction(VatomManager.Action.ACQUIRE, JSONObject().put("this.id", id))

  override fun transferVatom(id: String, tokenType: VatomManager.TokenType, token: String): Completable {
    val payload = JSONObject()
    payload.put("this.id", id)
    when (tokenType) {
      VatomManager.TokenType.EMAIL -> payload.put("new.owner.email", token)
      VatomManager.TokenType.PHONE_NUMBER -> payload.put("new.owner.phone_number", token)
      VatomManager.TokenType.ID -> payload.put("new.owner.email", token)
    }
    return Completable.fromSingle(preformAction(VatomManager.Action.TRANSFER, payload))
  }

  override fun cloneVatom(id: String, tokenType: VatomManager.TokenType, token: String): Completable {
    val payload = JSONObject()
    payload.put("this.id", id)
    when (tokenType) {
      VatomManager.TokenType.EMAIL -> payload.put("new.owner.email", token)
      VatomManager.TokenType.PHONE_NUMBER -> payload.put("new.owner.phone_number", token)
      VatomManager.TokenType.ID -> payload.put("new.owner.email", token)
    }
    return Completable.fromSingle(preformAction(VatomManager.Action.CLONE, payload))
  }

  override fun dropVatom(id: String, latitude: Double, longitude: Double): Completable {
    val payload = JSONObject()
    payload.put("this.id", id)
    payload.put(
      "geo.pos", JSONObject()
        .put("lat", latitude)
        .put("lon", longitude)
    )
    return Completable.fromSingle(preformAction(VatomManager.Action.DROP, payload))
  }

  override fun pickupVatom(id: String): Completable =
    Completable.fromSingle(
      preformAction(
        VatomManager.Action.PICKUP,
        JSONObject().put("this.id", id)
      )
    )

  override fun discover(query: JSONObject): Single<List<Vatom>> = Single.fromCallable {
    query.put(
      "return",
      JSONObject()
        .put("type", DiscoverQueryBuilder.ResultType.PAYLOAD)
        .put("fields", JSONArray())
    )
    api.discover(query).payload.vatoms
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun trashVatom(id: String): Completable = Completable.fromCallable {
    api.trashVatom(TrashVatomRequest(id))
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun updateVatom(vatom: Vatom, update: StateUpdateEvent): Single<Vatom> {

    return Single.fromCallable {

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
      .subscribeOn(Schedulers.computation())
      .observeOn(AndroidSchedulers.mainThread())
  }

}