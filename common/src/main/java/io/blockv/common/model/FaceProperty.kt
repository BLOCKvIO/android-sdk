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

import io.blockv.common.internal.json.JsonModule
import org.json.JSONObject

class FaceProperty {
  @JsonModule.Serialize(name = "display_url")
  val displayUrl: String
  @JsonModule.Serialize(name = "view_mode", path = "constraints")
  val viewMode: String
  @JsonModule.Serialize(path = "constraints")
  val platform: String
  @JsonModule.Serialize
  val config: JSONObject
  @JsonModule.Serialize
  val resources: List<String>

  @JsonModule.Serializable
  constructor(
    displayUrl: String,
    viewMode: String,
    platform: String,
    config: JSONObject,
    resources: List<String>
  ) {
    this.displayUrl = displayUrl
    this.viewMode = viewMode
    this.platform = platform
    this.config = config
    this.resources = resources

  }

  override fun toString(): String {
    return "FaceProperty{" +
      "displayUrl='" + displayUrl + '\'' +
      ", viewMode='" + viewMode + '\'' +
      ", platform='" + platform + '\'' +
      ", config='" + config + '\'' +
      ", resources='" + resources + '\'' +
      "}"
  }
}

