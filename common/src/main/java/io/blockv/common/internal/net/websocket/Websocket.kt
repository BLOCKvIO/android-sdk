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
package io.blockv.common.internal.net.websocket

import io.blockv.common.model.WebSocketEvent
import org.json.JSONObject

interface Websocket {

  fun connect(listener: WebSocketListener)

  fun disconnect()

  interface WebSocketListener {

    fun onEvent(event: WebSocketEvent<JSONObject>)

    fun onError(throwable: Throwable)
  }
}