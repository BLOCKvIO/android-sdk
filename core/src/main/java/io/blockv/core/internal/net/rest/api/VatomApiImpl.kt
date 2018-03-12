package io.blockv.core.internal.net.rest.api

import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.Client
import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.request.PerformActionRequest
import io.blockv.core.internal.net.rest.request.VatomRequest
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.Action
import io.blockv.core.model.Group
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/25.
 */
class VatomApiImpl(val client: Client,
                   val jsonModule: JsonModule) : VatomApi {

  override fun getUserVatom(request: VatomRequest): BaseResponse<Group?> {
    val response: JSONObject = client.post("v1/user/vatom/get", request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")


    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      (if (payload != null) jsonModule.groupDeserilizer.deserialize(payload) else null) ?: Group(ArrayList(), ArrayList(), ArrayList())
    )
  }

  override fun discover(request: JSONObject): BaseResponse<JSONObject?> {
    val response: JSONObject = client.post("v1/vatom/discover", request)
    val payload: JSONObject? = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      payload)
  }


  override fun getUserInventory(request: InventoryRequest): BaseResponse<Group?> {
    val response: JSONObject = client.post("/v1/user/vatom/inventory", request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")

    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      if (payload != null) jsonModule.groupDeserilizer.deserialize(payload) else null)

  }

  override fun getVatomActions(template: String?): BaseResponse<List<Action>> {
    val response: JSONObject = client.get("v1/user/actions/" + template)
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
    val response: JSONObject = client.post("v1/user/vatom/action/" + request.action, request.toJson())
    val payload: JSONObject? = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      payload)
  }
}