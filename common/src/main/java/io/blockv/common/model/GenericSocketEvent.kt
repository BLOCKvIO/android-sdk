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
package io.blockv.common.model

import io.blockv.common.internal.json.serializer.Serializer
import org.json.JSONObject

class GenericSocketEvent : WebSocketEvent<JSONObject>, Model {

  @Serializer.Serialize(name = "msg_type")
  override val messageType: String
  @Serializer.Serialize(name = "user_id")
  override val userId: String
  @Serializer.Serialize(name = "payload")
  override val payload: JSONObject

  @Serializer.Serializable
  constructor(
    messageType: String,
    userId: String,
    payload: JSONObject
  ) : super(messageType, userId, payload) {

    this.messageType = messageType
    this.userId = userId
    this.payload = payload
  }

}