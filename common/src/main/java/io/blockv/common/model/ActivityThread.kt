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

class ActivityThread @Serializer.Serializable constructor(
  @Serializer.Serialize(name = "name")
  val id: String,
  @Serializer.Serialize(name = "when_modified")
  val whenModified: Long,
  @Serializer.Serialize(name = "last_message")
  val lastMessage: ActivityMessage,
  @Serializer.Serialize(name = "last_message_user_info")
  val lastMessageUser: UserInfo
) : Model {

  class UserInfo @Serializer.Serializable constructor(
    @Serializer.Serialize
    val name: String,
    @Serializer.Serialize(name = "avatar_uri")
    val avatarUri: String
  ) {

    override fun toString(): String {
      return "UserInfo{" +
        "name='" + name + '\'' +
        ", avatarUri='" + avatarUri + '\'' +
        "}"
    }
  }

  override fun toString(): String {
    return "ActivityThread{" +
      "id='" + id + '\'' +
      ", whenModified='" + whenModified + '\'' +
      ", lastMessage='" + lastMessage + '\'' +
      ", lastMessageUser='" + lastMessageUser + '\'' +
      "}"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ActivityThread) return false
    return id == id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

}