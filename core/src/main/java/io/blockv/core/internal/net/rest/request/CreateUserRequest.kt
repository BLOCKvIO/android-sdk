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
package io.blockv.core.internal.net.rest.request

import org.json.JSONArray
import org.json.JSONObject

class CreateUserRequest {

  var firstName: String? = null
  var lastName: String? = null
  var birthday: String? = null
  var avatarUri: String? = null
  var password: String? = null
  var language: String? = null
  var tokens: JSONArray? = null

  constructor(firstName: String?,
              lastName: String?,
              birthday: String?,
              avatarUri: String?,
              password: String?,
              language: String?,
              tokens: JSONArray?) {
    this.firstName = firstName
    this.lastName = lastName
    this.birthday = birthday
    this.avatarUri = avatarUri
    this.password = password
    this.language = language
    this.tokens = tokens
  }


  fun toJson(): JSONObject {
    val json: JSONObject = JSONObject()
    if (firstName != null) json.put("first_name", firstName)
    if (lastName != null) json.put("last_name", lastName)
    if (birthday != null) json.put("birthday", birthday)
    if (avatarUri != null) json.put("avatar_uri", avatarUri)
    if (password != null) json.put("password", password)
    if (language != null) json.put("language", language)
    if (tokens != null) json.put("user_tokens", tokens)

    return json
  }

}
