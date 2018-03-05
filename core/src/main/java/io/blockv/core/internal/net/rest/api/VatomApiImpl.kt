package io.blockv.core.internal.net.rest.api

import io.blockv.core.internal.net.rest.Client
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.request.GeoRequest
import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.request.PerformActionRequest
import io.blockv.core.internal.net.rest.request.VatomRequest
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.Action
import io.blockv.core.model.Inventory
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/25.
 */
class VatomApiImpl(val client: Client,
                   val jsonModule: JsonModule) : VatomApi {

  override fun getCurrentUserVatom(request: VatomRequest): BaseResponse<Inventory?> {
    val response: JSONObject = client.post("v1/currentuser/vatom/get", request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")


    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      (if (payload != null) jsonModule.inventoryDeserilizer.deserialize(payload) else null) ?: Inventory()
    )
  }

  override fun discover(request: JSONObject): BaseResponse<JSONObject?> {
    val response: JSONObject = client.post("v1/currentuser/vatom/discover", request)
    val payload: JSONObject? = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      payload)
  }


  override fun getCurrentUserInventory(request: InventoryRequest): BaseResponse<Inventory?> {
    val response: JSONObject = client.post("v1/currentuser/inventory", request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")

    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      (if (payload != null) jsonModule.inventoryDeserilizer.deserialize(payload) else null) ?: Inventory()
    )
  }

  override fun getVatomActions(template: String?): BaseResponse<List<Action>> {
    val response: JSONObject = client.get("v1/currentuser/actions/" + template)
    val payload: JSONArray? = response.optJSONArray("payload")
    val list: ArrayList<Action> = ArrayList()
    if (payload != null) {
      var count = 0
      while (count < payload.length()) {
        val action: Action? = jsonModule.actionDeserilizer.deserialize(payload.getJSONObject(count))
        if (action != null) {
          list.add(action)
        }
        count++
      }
    }
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      list)
  }

  override fun preformAction(request: PerformActionRequest): BaseResponse<JSONObject?> {
    val response: JSONObject = client.post("api/v1/currentuser/action/"+request.action, request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      payload)
  }

  override fun geoDiscover(request: GeoRequest): BaseResponse<Inventory?> {
    val response: JSONObject = client.post("v1/currentuser/geodiscover", request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")

    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      (if (payload != null) jsonModule.inventoryDeserilizer.deserialize(payload) else null) ?: Inventory()
    )
  }
}