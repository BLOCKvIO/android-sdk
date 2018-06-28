package io.blockv.core.internal.net.rest.api

import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.Client
import io.blockv.core.internal.net.rest.request.ActivityMessageListRequest
import io.blockv.core.internal.net.rest.request.ActivityThreadListRequest
import io.blockv.core.internal.net.rest.request.SendMessageRequest
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.ActivityMessageList
import io.blockv.core.model.ActivityThreadList
import org.json.JSONObject
import java.util.*

class ActivityApiImpl(val client: Client,
                      val jsonModule: JsonModule) : ActivityApi {

  override fun getThreadList(request: ActivityThreadListRequest): BaseResponse<ActivityThreadList> {
    val response: JSONObject = client.post("v1/activity/mythreads", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload") ?: JSONObject()

    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.activityThreadListDeserializer.deserialize(payload) ?: ActivityThreadList("", ArrayList()))
  }

  override fun getThreadMessages(request: ActivityMessageListRequest): BaseResponse<ActivityMessageList> {
    val response: JSONObject = client.post("v1/activity/mythreadmessages", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload") ?: JSONObject()

    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.activityMessageListDeserializer.deserialize(payload) ?: ActivityMessageList("", ArrayList()))
  }

  override fun sendMessage(request: SendMessageRequest): BaseResponse<Void?> {
    val response: JSONObject = client.post("v1/user/message", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")

    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      null)

  }
}