package io.blockv.core.internal.net.rest.api

import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.internal.net.rest.request.GeoRequest
import io.blockv.core.model.Action
import io.blockv.core.model.Inventory
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/21.
 */
interface VatomApi {

  fun getCurrentUserInventory(request: InventoryRequest): BaseResponse<Inventory?>

  fun getVatomActions(template:String?): BaseResponse<List<Action>>

  fun preformAction(request: JSONObject): BaseResponse<JSONObject?>

  fun geoDiscover(request: JSONObject): BaseResponse<Inventory?>

  fun discover(request: JSONObject): BaseResponse<JSONObject?>



}