/**
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

import io.blockv.core.client.manager.ResourceManager
import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.request.PerformActionRequest
import io.blockv.core.internal.net.rest.request.VatomRequest
import io.blockv.core.model.Action
import io.blockv.core.model.Group
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class VatomManagerImpl(val api: VatomApi,
                       val resourceManager: ResourceManager) : VatomManager {
  override fun getVatoms(vararg ids: String): Single<Group> = Single.fromCallable {
    val group = api.getUserVatom(VatomRequest(ids.toList())).payload ?: Group(ArrayList(), ArrayList(), ArrayList())
    group.vatoms.forEach {
      it.property.resources?.forEach {
        it.url = resourceManager.encodeUrl(it.url) ?: it.url
      }
    }
    group
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getInventory(id: String?): Single<Group> = Single.fromCallable {
    val group = api.getUserInventory(InventoryRequest((if (id == null || id.isEmpty()) "." else id))).payload
    group?.vatoms?.forEach {
      it.property.resources.forEach {
        it.url = resourceManager.encodeUrl(it.url) ?: it.url
      }
    }
    group ?: Group(ArrayList(), ArrayList(), ArrayList())
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getVatomActions(templateId: String): Single<List<Action>> = Single.fromCallable {
    api.getVatomActions(templateId).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun preformAction(action: String, id: String, payload: JSONObject?): Completable = Completable.fromCallable {
    api.preformAction(PerformActionRequest(action, id, payload))
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun preformAction(action: io.blockv.core.client.manager.VatomManager.Action, id: String, payload: JSONObject?): Completable = preformAction(action.action(), id, payload)

  override fun acquireVatom(id: String): Completable = preformAction(io.blockv.core.client.manager.VatomManager.Action.ACQUIRE, id, null)

  override fun transferVatom(id: String, tokenType: io.blockv.core.client.manager.VatomManager.TokenType, token: String): Completable {
    val payload = JSONObject()
    when (tokenType) {
      io.blockv.core.client.manager.VatomManager.TokenType.EMAIL -> payload.put("new.owner.email", token)
      io.blockv.core.client.manager.VatomManager.TokenType.PHONE_NUMBER -> payload.put("new.owner.phone_number", token)
      io.blockv.core.client.manager.VatomManager.TokenType.ID -> payload.put("new.owner.email", token)
    }

    return preformAction(io.blockv.core.client.manager.VatomManager.Action.TRANSFER, id, payload)
  }


  override fun dropVatom(id: String, latitude: Double, longitude: Double): Completable {
    val payload = JSONObject()
    payload.put("geo.pos", JSONObject()
      .put("Lat", latitude)
      .put("Lon", longitude))
    return preformAction(io.blockv.core.client.manager.VatomManager.Action.DROP, id, payload)
  }


  override fun pickupVatom(id: String): Completable = preformAction(io.blockv.core.client.manager.VatomManager.Action.PICKUP, id, null)

  override fun discover(query: JSONObject): Single<Group> = Single.fromCallable {
    val group = api.discover(query).payload ?: Group(ArrayList(), ArrayList(), ArrayList())
    group.vatoms.forEach {
      it.property.resources?.forEach {
        it.url = resourceManager.encodeUrl(it.url) ?: it.url
      }
    }
    group
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

}