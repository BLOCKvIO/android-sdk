package io.blockv.core.internal.net.rest.api

import io.blockv.android.core.internal.net.rest.Client
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.internal.net.rest.request.GeoRequest
import io.blockv.core.model.Action
import io.blockv.core.model.Inventory
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/25.
 */
class VatomApiImpl(val client: Client,
                   val jsonModule: JsonModule) : VatomApi {


  override fun getCurrentUserInventory(request: InventoryRequest): BaseResponse<Inventory?> {
    val response: JSONObject = client.post("v1/currentuser/inventory", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.inventoryDeserilizer.deserialize(payload))
  }

  override fun getVatomActions(template: String?): BaseResponse<List<Action>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun preformAction(request: JSONObject): BaseResponse<Void?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun geoDiscover(request: GeoRequest): BaseResponse<Inventory?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}