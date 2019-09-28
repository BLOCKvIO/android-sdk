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

class FaceProperty @Serializer.Serializable constructor(
  @Serializer.Serialize(name = "display_url")
  val displayUrl: String,
  @Serializer.Serialize(name = "view_mode", path = "constraints")
  val viewMode: String,
  @Serializer.Serialize(path = "constraints")
  val platform: String,
  @Serializer.Serialize(path = "constraints")
  val quality: String,
  @Serializer.Serialize
  val config: JSONObject,
  @Serializer.Serialize
  val resources: List<String>
) : Model {

  override fun toString(): String {
    return "FaceProperty{" +
      "displayUrl='" + displayUrl + '\'' +
      ", viewMode='" + viewMode + '\'' +
      ", platform='" + platform + '\'' +
      ", quality='" + quality + '\'' +
      ", config='" + config + '\'' +
      ", resources='" + resources + '\'' +
      "}"
  }
}

