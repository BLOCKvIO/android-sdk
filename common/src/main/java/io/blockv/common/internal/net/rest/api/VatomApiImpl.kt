/*
 *  BlockV AG. Copyright (c) 2 018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.common.internal.net.rest.api

import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.Client
import io.blockv.common.internal.net.rest.request.*
import io.blockv.common.internal.net.rest.response.BaseResponse
import io.blockv.common.model.*
import org.json.JSONArray
import org.json.JSONObject

class VatomApiImpl(
  val client: Client,
  val jsonModule: JsonModule
) : VatomApi {
  override fun updateVatom(request: JSONObject): BaseResponse<VatomUpdate> {
    val response: JSONObject = client.patch("v1/vatoms", request)
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(response.getJSONObject("payload"))
    )
  }

  override fun geoDiscover(request: GeoRequest): BaseResponse<List<Vatom>> {
    val response: JSONObject = client.post("v1/vatom/geodiscover", request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")

    val pack =
      if (payload != null)
        jsonModule.deserialize<Pack>(payload)
      else
        null

    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      if (pack != null) combineVatomProperties(pack.vatoms, pack.faces, pack.actions)
      else
        ArrayList()
    )
  }

  override fun geoGroupDiscover(request: GeoGroupRequest): BaseResponse<List<GeoGroup>> {
    val response: JSONObject = client.post("v1/vatom/geodiscovergroups", request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")
    val group: JSONArray = payload?.optJSONArray("groups") ?: JSONArray()
    val list: ArrayList<GeoGroup> = ArrayList()
    if (payload != null) {
      var count = 0
      while (count < group.length()) {
        val geoGroup: GeoGroup? = jsonModule.deserialize(group.getJSONObject(count))
        if (geoGroup != null) {
          list.add(geoGroup)
        }
        count++
      }
    }
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      list
    )
  }

  override fun getUserVatom(request: VatomRequest): BaseResponse<List<Vatom>> {
    val response: JSONObject = client.post("v1/user/vatom/get", request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")

    val pack =
      if (payload != null)
        jsonModule.deserialize<Pack>(payload)
      else
        null

    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      if (pack != null) combineVatomProperties(pack.vatoms, pack.faces, pack.actions)
      else
        ArrayList()
    )
  }

  override fun discover(request: JSONObject): BaseResponse<DiscoverPack> {
    val response: JSONObject = client.post("v1/vatom/discover", request)
    val payload: JSONObject? = response.optJSONObject("payload")

    val discoverPack = if (payload != null) {
      jsonModule.deserialize<DiscoverPack>(payload)
    } else
      null

    if (discoverPack != null) {
      combineVatomProperties(discoverPack.vatoms, discoverPack.faces, discoverPack.actions)
    }
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      discoverPack ?: DiscoverPack(0, ArrayList())
    )
  }


  override fun getUserInventory(request: InventoryRequest): BaseResponse<List<Vatom>> {
    val response: JSONObject = client.post("/v1/user/vatom/inventory", request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")

    val pack =
      if (payload != null)
        jsonModule.deserialize<Pack>(payload)
      else
        null

    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      if (pack != null) combineVatomProperties(pack.vatoms, pack.faces, pack.actions)
      else
        ArrayList()
    )

  }

  override fun getVatomActions(template: String?): BaseResponse<List<Action>> {
    val response: JSONObject = client.get("v1/user/actions/$template")
    val payload: JSONArray? = response.optJSONArray("payload")
    val list: ArrayList<Action> = ArrayList()
    if (payload != null) {
      var count = 0
      while (count < payload.length()) {
        val action: Action? = jsonModule.deserialize(payload.getJSONObject(count))
        if (action != null) {
          list.add(action)
        }
        count++
      }
    }
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      list
    )
  }

  override fun preformAction(request: PerformActionRequest): BaseResponse<JSONObject?> {
    val response: JSONObject = client.post("v1/user/vatom/action/" + request.action, request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      payload
    )
  }

  override fun trashVatom(request: TrashVatomRequest): BaseResponse<JSONObject> {
    val response: JSONObject = client.post("/v1/user/vatom/trash", request.toJson())
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      response
    )
  }

  private fun combineVatomProperties(vatoms: List<Vatom>, faces: List<Face>, actions: List<Action>): List<Vatom> {

    val faceMap: HashMap<String, ArrayList<Face>> = HashMap()
    val actionMap: HashMap<String, ArrayList<Action>> = HashMap()

    faces.forEach { face ->
      if (!faceMap.containsKey(face.templateId)) {
        faceMap[face.templateId] = ArrayList()
      }
      faceMap[face.templateId]?.add(face)
    }

    actions.forEach { action ->
      if (!actionMap.containsKey(action.templateId)) {
        actionMap[action.templateId] = ArrayList()
      }
      actionMap[action.templateId]?.add(action)
    }

    vatoms.forEach { vatom ->
      vatom.faces = faceMap[vatom.property.templateId] ?: vatom.faces
      vatom.actions = actionMap[vatom.property.templateId] ?: vatom.actions
    }

    return vatoms
  }
}