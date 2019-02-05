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

open class PublicUser : Model {

  @Serializer.Serialize
  var id: String? = null
  @Serializer.Serialize(name = "first_name", path = "properties")
  var firstName: String? = null
  @Serializer.Serialize(name = "last_name", path = "properties")
  var lastName: String? = null
  @Serializer.Serialize(name = "avatar_uri", path = "properties")
  var avatarUri: String? = null

  val name: String
    get() = ((this.firstName ?: "") + " " + (this.lastName ?: "")).trim()

  constructor()

  constructor(id: String?, firstName: String?, lastName: String?, avatarUri: String?) {
    this.id = id
    this.firstName = firstName
    this.lastName = lastName
    this.avatarUri = avatarUri
  }


  override fun toString(): String {
    return "User{" +
      "id='" + id + '\'' +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", avatarUrl='" + avatarUri + '\'' +
      '}'
  }

}
