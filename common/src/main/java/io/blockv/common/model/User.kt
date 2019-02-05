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

class User : Model {

  @Serializer.Serialize
  val id: String
  @Serializer.Serialize(name = "when_created", path = "meta")
  val whenCreated: String
  @Serializer.Serialize(name = "when_modified", path = "meta")
  val whenModified: String
  @Serializer.Serialize(name = "first_name", path = "properties")
  val firstName: String
  @Serializer.Serialize(name = "last_name", path = "properties")
  val lastName: String
  @Serializer.Serialize(name = "avatar_uri", path = "properties")
  val avatarUri: String
  @Serializer.Serialize(name = "birthday", path = "properties")
  val birthday: String
  @Serializer.Serialize(path = "properties")
  val language: String
  @Serializer.Serialize(name = "name_public", path = "properties")
  val isNamePublic: Boolean
  @Serializer.Serialize(name = "avatar_public", path = "properties")
  val isAvatarPublic: Boolean
  @Serializer.Serialize(name = "is_password_set", path = "properties")
  val isPasswordSet: Boolean
  @Serializer.Serialize(name = "nonpush_notification", path = "properties")
  val nonPushNotifications: Boolean

  @Serializer.Serializable
  constructor(
    id: String,
    whenCreated: String,
    whenModified: String,
    firstName: String,
    lastName: String,
    avatarUri: String,
    birthday: String,
    language: String,
    isNamePublic: Boolean,
    isAvatarPublic: Boolean,
    isPasswordSet: Boolean,
    nonPushNotifications: Boolean
  ) {

    this.id = id
    this.whenCreated = whenCreated
    this.whenModified = whenModified
    this.firstName = firstName
    this.lastName = lastName
    this.avatarUri = avatarUri
    this.birthday = birthday
    this.language = language
    this.isNamePublic = isNamePublic
    this.isAvatarPublic = isAvatarPublic
    this.isPasswordSet = isPasswordSet
    this.nonPushNotifications = nonPushNotifications
  }

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
