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

import org.json.JSONObject

class LoginRequest(var tokenType: String,
                   var token: String?,
                   var password: String?) {

  fun toJson(): JSONObject {
    val out: JSONObject = JSONObject()
    if (token != null) out.put("token", token)
    out.put("token_type", tokenType)
    if (password != null) out.put("auth_data", JSONObject().put("password", password))

    return out
  }
}