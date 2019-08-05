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

class Token @Serializer.Serializable constructor(
  @Serializer.Serialize
  var id: String,
  @Serializer.Serialize(name = "user_id", path = "properties")
  var userId: String,
  @Serializer.Serialize(name = "app_id", path = "properties")
  var appId: String,
  @Serializer.Serialize(name = "when_created", path = "meta")
  var whenCreated: String,
  @Serializer.Serialize(name = "when_modified", path = "meta")
  var whenModified: String,
  @Serializer.Serialize(name = "token_type", path = "properties")
  var tokenType: String,
  @Serializer.Serialize(path = "properties")
  var token: String,
  @Serializer.Serialize(name = "confirmed", path = "properties")
  var isConfirmed: Boolean,
  @Serializer.Serialize(name = "is_default", path = "properties")
  var isPrimary: Boolean,
  @Serializer.Serialize(name = "verify_code_expires", path = "properties")
  var verifyCodeExpires: String
) : Model {

  override fun toString(): String {
    return "Token{" +
      " id='" + id + '\'' +
      ", userId='" + userId + '\'' +
      ", appId='" + appId + '\'' +
      ", whenCreated='" + whenCreated + '\'' +
      ", whenModified='" + whenModified + '\'' +
      ", tokenType='" + tokenType + '\'' +
      ", token='" + token + '\'' +
      ", isConfirmed='" + isConfirmed + '\'' +
      ", isPrimary='" + isPrimary + '\'' +
      ", verifyCodeExpires='" + verifyCodeExpires + '\'' +
      "}"
  }
}
