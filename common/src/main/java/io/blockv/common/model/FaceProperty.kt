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

import org.json.JSONObject

class FaceProperty(
  val displayUrl: String,
  val viewMode: String,
  val platform: String,
  val config: JSONObject,
  val resources: List<String>
) {


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
