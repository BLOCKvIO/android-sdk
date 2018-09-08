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
package io.blockv.common.internal.net.rest.request

import org.json.JSONArray
import org.json.JSONObject

class CreateUserRequest(
  var firstName: String?,
  var lastName: String?,
  var birthday: String?,
  var avatarUri: String?,
  var password: String?,
  var language: String?,
  var tokens: JSONArray?
) {


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
