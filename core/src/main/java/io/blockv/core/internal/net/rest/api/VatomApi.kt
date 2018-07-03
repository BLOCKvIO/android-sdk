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
package io.blockv.core.internal.net.rest.api

import io.blockv.core.internal.net.rest.request.*
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.Action
import io.blockv.core.model.DiscoverPack
import io.blockv.core.model.GeoGroup
import io.blockv.core.model.Pack
import org.json.JSONObject

interface VatomApi {

  fun getUserVatom(request: VatomRequest): BaseResponse<Pack>

  fun getUserInventory(request: InventoryRequest): BaseResponse<Pack>

  fun getVatomActions(template: String?): BaseResponse<List<Action>>

  fun preformAction(request: PerformActionRequest): BaseResponse<JSONObject?>

  fun discover(request: JSONObject): BaseResponse<DiscoverPack>

  fun geoDiscover(request: GeoRequest): BaseResponse<Pack>

  fun geoGroupDiscover(request: GeoGroupRequest): BaseResponse<List<GeoGroup>>

  fun updateVatom(request: JSONObject): BaseResponse<Void?>
}

