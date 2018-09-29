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

class User(
  val id: String,
  val whenCreated: String,
  val whenModified: String,
  val firstName: String,
  val lastName: String,
  val avatarUri: String,
  val birthday: String,
  val language: String,
  val isNamePublic: Boolean,
  val isAvatarPublic: Boolean,
  val isPasswordSet: Boolean,
  val nonPushNotifications: Boolean
) {

  val name: String
    get() = (this.firstName + " " + this.lastName).trim()

  override fun toString(): String {
    return "User{" +
      "id='" + id + '\'' +
      ", whenCreated='" + whenCreated + '\'' +
      ", whenModified='" + whenModified + '\'' +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", avatarUrl='" + avatarUri + '\'' +
      ", birthday='" + birthday + '\'' +
      ", language='" + language + '\'' +
      ", isNamePublic='" + isNamePublic + '\'' +
      ", isAvatarPublic='" + isAvatarPublic + '\'' +
      ", isPasswordSet='" + isPasswordSet + '\'' +
      ", nonPushNotifications='" + nonPushNotifications + '\'' +
      '}'
  }

}
