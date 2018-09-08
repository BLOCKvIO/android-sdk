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

import io.blockv.common.internal.net.rest.request.*
import io.blockv.common.internal.net.rest.response.BaseResponse
import io.blockv.common.model.*
import org.json.JSONObject

interface VatomApi {

  fun getUserVatom(request: VatomRequest): BaseResponse<List<Vatom>>

  fun getUserInventory(request: InventoryRequest): BaseResponse<List<Vatom>>

  fun getVatomActions(template: String?): BaseResponse<List<Action>>

  fun preformAction(request: PerformActionRequest): BaseResponse<JSONObject?>

  fun discover(request: JSONObject): BaseResponse<DiscoverPack>

  fun geoDiscover(request: GeoRequest): BaseResponse<List<Vatom>>

  fun geoGroupDiscover(request: GeoGroupRequest): BaseResponse<List<GeoGroup>>

  fun updateVatom(request: JSONObject): BaseResponse<Void?>

  fun trashVatom(request: TrashVatomRequest): BaseResponse<JSONObject>
}

