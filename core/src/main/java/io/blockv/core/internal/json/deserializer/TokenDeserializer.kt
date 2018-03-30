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
package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.Token

class TokenDeserializer : Deserializer<Token> {
  override fun deserialize(data: org.json.JSONObject): Token? {
    try {
      val properties: org.json.JSONObject = data.getJSONObject("properties")
      val token: String? = properties.getString("token")
      val tokenType: String = properties.getString("token_type")
      val confirmed: Boolean? = data.optBoolean("confirmed", false)
      return Token(
        tokenType,
        token,
        confirmed)
    } catch (e: Exception) {
      android.util.Log.w("TokenDeserializer", e.message)
    }
    return null
  }

}