/**
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.model

class User {

  var id: String? = null
  var whenCreated: String? = null
  var whenModified: String? = null
  var firstName: String? = null
  var lastName: String? = null
  var avatarUri: String? = null
  var birthday: String? = null
  var language: String? = null
  val name: String
    get() = ((this.firstName ?: "") + " " + (this.lastName ?: "")).trim()


  constructor(id: String?,
              whenCreated: String?,
              whenModified: String?,
              firstName: String?,
              lastName: String?,
              avatarUri: String?,
              birthday: String?,
              language: String?) {
    this.id = id
    this.whenCreated = whenCreated
    this.whenModified = whenModified
    this.firstName = firstName
    this.lastName = lastName
    this.avatarUri = avatarUri
    this.birthday = birthday
    this.language = language
  }

  constructor()


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
      '}'
  }

}
