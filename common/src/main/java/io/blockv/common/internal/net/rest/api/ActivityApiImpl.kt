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

import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.Client
import io.blockv.common.internal.net.rest.request.ActivityMessageListRequest
import io.blockv.common.internal.net.rest.request.ActivityThreadListRequest
import io.blockv.common.internal.net.rest.request.SendMessageRequest
import io.blockv.common.internal.net.rest.response.BaseResponse
import io.blockv.common.model.ActivityMessageList
import io.blockv.common.model.ActivityThreadList
import org.json.JSONObject

class ActivityApiImpl(
  val client: Client,
  val jsonModule: JsonModule
) : ActivityApi {

  override fun getThreadList(request: ActivityThreadListRequest): BaseResponse<ActivityThreadList> {
    val response: JSONObject = client.post("v1/activity/mythreads", request.toJson())
    val payload: JSONObject = response.getJSONObject("payload")

    return BaseResponse(
      response.getString("request_id"),
      jsonModule.deserialize(payload)
    )
  }

  override fun getThreadMessages(request: ActivityMessageListRequest): BaseResponse<ActivityMessageList> {
    val response: JSONObject = client.post("v1/activity/mythreadmessages", request.toJson())
    val payload: JSONObject = response.getJSONObject("payload")

    return BaseResponse(
      response.getString("request_id"),
      jsonModule.deserialize(payload)
    )
  }

  override fun sendMessage(request: SendMessageRequest): BaseResponse<Unit> {
    val response: JSONObject = client.post("v1/user/message", request.toJson())

    return BaseResponse(
      response.getString("request_id"),
      Unit
    )
  }
}