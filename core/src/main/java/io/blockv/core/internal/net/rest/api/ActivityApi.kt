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