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
package io.blockv.rx.client.manager

import io.blockv.core.client.manager.VatomManager.*
import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.internal.net.rest.request.*
import io.blockv.core.model.DiscoverPack
import io.blockv.core.model.GeoGroup
import io.blockv.core.model.Pack
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class VatomManagerImpl(val api: VatomApi) : VatomManager {

  override fun geoDiscover(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double,
    filter: GeoFilter
  ): Single<Pack> = Single.fromCallable {
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
    filter: GeoFilter
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

  override fun getVatoms(vararg ids: String): Single<Pack> = Single.fromCallable {
    api.getUserVatom(VatomRequest(ids.toList())).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getInventory(id: String?, page: Int, limit: Int): Single<Pack> = Single.fromCallable {
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

  override fun getVatomActions(templateId: String): Single<List<io.blockv.core.model.Action>> = Single.fromCallable {
    api.getVatomActions(templateId).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun preformAction(
    action: String,
    id: String,
    payload: JSONObject?
  ): Single<JSONObject> = Single.fromCallable {
    api.preformAction(PerformActionRequest(action, id, payload)).payload ?: JSONObject()
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun preformAction(
    action: Action,
    id: String,
    payload: JSONObject?
  ): Single<JSONObject> = preformAction(action.action(), id, payload)

  override fun acquireVatom(id: String): Single<JSONObject> = preformAction(Action.ACQUIRE, id, null)

  override fun transferVatom(id: String, tokenType: TokenType, token: String): Completable {
    val payload = JSONObject()
    when (tokenType) {
      TokenType.EMAIL -> payload.put("new.owner.email", token)
      TokenType.PHONE_NUMBER -> payload.put("new.owner.phone_number", token)
      TokenType.ID -> payload.put("new.owner.email", token)
    }
    return preformAction(Action.TRANSFER, id, payload).toCompletable()
  }

  override fun dropVatom(id: String, latitude: Double, longitude: Double): Completable {
    val payload = JSONObject()
    payload.put(
      "geo.pos", JSONObject()
        .put("lat", latitude)
        .put("lon", longitude)
    )
    return preformAction(Action.DROP, id, payload).toCompletable()
  }

  override fun pickupVatom(id: String): Completable = preformAction(Action.PICKUP, id, null).toCompletable()

  override fun discover(query: JSONObject): Single<DiscoverPack> = Single.fromCallable {
    api.discover(query).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun trashVatom(id: String): Completable = Completable.fromCallable {
    api.trashVatom(TrashVatomRequest(id))
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

}