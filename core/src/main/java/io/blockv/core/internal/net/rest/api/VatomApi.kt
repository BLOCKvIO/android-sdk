package io.blockv.core.internal.net.rest.api

import io.blockv.core.internal.net.rest.request.InventoryRequest
import io.blockv.core.internal.net.rest.request.PerformActionRequest
import io.blockv.core.internal.net.rest.request.VatomRequest
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.Action
import io.blockv.core.model.DiscoverGroup
import io.blockv.core.model.Group
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/21.
 */
interface VatomApi {

  fun getUserVatom(request: VatomRequest): BaseResponse<Group?>

  fun getUserInventory(request: InventoryRequest): BaseResponse<Group?>

  fun getVatomActions(template: String?): BaseResponse<List<Action>>

  fun preformAction(request:PerformActionRequest): BaseResponse<JSONObject?>

  fun discover(request: JSONObject): BaseResponse<DiscoverGroup?>


}