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
package io.blockv.common.internal.json.deserializer.user

import io.blockv.common.internal.json.deserializer.Deserializer
import io.blockv.common.model.Token

class TokenDeserializer : Deserializer<Token> {
  override fun deserialize(data: org.json.JSONObject): Token? {
    try {
      val id: String = data.getString("id")
      val meta: org.json.JSONObject = data.getJSONObject("meta")
      val whenCreated: String = meta.getString("when_created")
      val whenModified: String = meta.getString("when_modified")
      val properties: org.json.JSONObject = data.getJSONObject("properties")
      val appId: String = properties.getString("app_id")
      val token: String = properties.getString("token")
      val tokenType: String = properties.getString("token_type")
      val confirmed: Boolean = properties.optBoolean("confirmed", false)
      val primary: Boolean = properties.optBoolean("is_default", false)
      val userId: String = properties.getString("user_id")
      val verifyCodeExpires: String = properties.optString("verify_code_expires", "")

      return Token(
        id,
        userId,
        appId,
        whenCreated,
        whenModified,
        tokenType,
        token,
        confirmed,
        primary,
        verifyCodeExpires
      )

    } catch (e: Exception) {
      android.util.Log.w("TokenDeserializer", e.message)
    }
    return null
  }

}