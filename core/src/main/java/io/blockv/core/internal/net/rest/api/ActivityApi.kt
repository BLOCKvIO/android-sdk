package io.blockv.core.internal.net.rest.api

import io.blockv.core.internal.net.rest.request.ActivityMessageListRequest
import io.blockv.core.internal.net.rest.request.ActivityThreadListRequest
import io.blockv.core.internal.net.rest.request.SendMessageRequest
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.activity.ActivityMessageList
import io.blockv.core.model.activity.ActivityThreadList

interface ActivityApi {

  fun getThreadList(request: ActivityThreadListRequest): BaseResponse<ActivityThreadList>

  fun getThreadMessages(request: ActivityMessageListRequest): BaseResponse<ActivityMessageList>

  fun sendMessage(request: SendMessageRequest): BaseResponse<Void?>
}