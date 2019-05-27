/*
 *  BlockV AG. Copyright (c) 2019, all rights reserved.
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
import io.blockv.common.internal.net.rest.response.BaseResponse
import io.blockv.common.model.AppVersion
import org.json.JSONObject

class AppApiImpl(
  val client: Client,
  val jsonModule: JsonModule
) : AppApi {

  override fun getAppVersion(): BaseResponse<AppVersion> {
    val response: JSONObject = client.get("v1/general/app/version")
    val payload: JSONObject = response.getJSONObject("payload")
    return BaseResponse(
      response.getString("request_id"),
      jsonModule.deserialize(payload)
    )
  }
}