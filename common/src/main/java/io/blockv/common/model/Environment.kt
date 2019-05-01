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

open class Environment : Model {
  @Serializer.Serialize
  val rest: String
  @Serializer.Serialize
  val wss: String
  @Serializer.Serialize(name = "app_id")
  val appId: String
  @Serializer.Serialize(name = "redirect_uri")
  val redirectUri: String

  @Serializer.Serializable
  constructor(rest: String, wss: String, appId: String, redirectUri: String) {
    this.rest = rest
    this.wss = wss
    this.appId = appId
    this.redirectUri = redirectUri
  }

  companion object {
    val DEFAULT_SERVER = "https://api.blockv.io/"
    val DEFAULT_WEBSOCKET = "wss://newws.blockv.io/ws"
  }

  override fun toString(): String {
    return "Environment{" +
      " rest='" + rest + '\'' +
      ", wss='" + wss + '\'' +
      ", appId='" + appId + '\'' +
      "}"
  }

}