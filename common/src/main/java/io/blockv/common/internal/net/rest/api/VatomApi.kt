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
package io.blockv.common.internal.net.rest.api

import io.blockv.common.internal.net.rest.request.GeoGroupRequest
import io.blockv.common.internal.net.rest.request.GeoRequest
import io.blockv.common.internal.net.rest.request.InventoryRequest
import io.blockv.common.internal.net.rest.request.InventorySyncRequest
import io.blockv.common.internal.net.rest.request.PerformActionRequest
import io.blockv.common.internal.net.rest.request.RedeemRequest
import io.blockv.common.internal.net.rest.request.TrashVatomRequest
import io.blockv.common.internal.net.rest.request.VatomRequest
import io.blockv.common.internal.net.rest.response.BaseResponse
import io.blockv.common.model.Action
import io.blockv.common.model.DiscoverPack
import io.blockv.common.model.GeoGroup
import io.blockv.common.model.InventorySync
import io.blockv.common.model.Vatom
import io.blockv.common.model.VatomUpdate
import org.json.JSONObject

interface VatomApi {

  fun getUserVatom(request: VatomRequest): BaseResponse<List<Vatom>>

  fun getVatomJson(request: VatomRequest): BaseResponse<JSONObject>

  fun getUserInventory(request: InventoryRequest): BaseResponse<List<Vatom>>

  fun getInventoryJson(request: InventoryRequest): BaseResponse<JSONObject>

  fun getVatomActions(template: String?): BaseResponse<List<Action>>

  fun preformAction(request: PerformActionRequest): BaseResponse<JSONObject>

  fun discover(request: JSONObject): BaseResponse<DiscoverPack>

  fun discoverJson(request: JSONObject): BaseResponse<JSONObject>

  fun geoDiscover(request: GeoRequest): BaseResponse<List<Vatom>>

  fun geoDiscoverJson(request: GeoRequest): BaseResponse<JSONObject>

  fun geoGroupDiscover(request: GeoGroupRequest): BaseResponse<List<GeoGroup>>

  fun updateVatom(request: JSONObject): BaseResponse<VatomUpdate>

  fun trashVatom(request: TrashVatomRequest): BaseResponse<JSONObject>

  fun getInventoryHash(): BaseResponse<String>

  fun getInventorySync(request: InventorySyncRequest): BaseResponse<InventorySync>

  fun requestRedeem(request: RedeemRequest): BaseResponse<JSONObject>
}

