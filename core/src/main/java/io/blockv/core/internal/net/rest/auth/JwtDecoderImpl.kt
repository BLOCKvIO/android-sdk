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
package io.blockv.core.internal.net.rest.auth

import android.util.Base64
import android.util.Log
import io.blockv.core.model.DecodedJwt
import io.blockv.core.model.Jwt
import org.json.JSONObject
import java.util.*

class JwtDecoderImpl : JwtDecoder {
  override fun decode(jwt: Jwt): DecodedJwt {
    val parts: List<String> = jwt.token.split(".")
    try {
      if (parts.size == 3) {
        val data = JSONObject(String(Base64.decode(parts[1].toByteArray(), Base64.DEFAULT)))
        return DecodedJwt(jwt, data.getString("user_id"), Date(data.getLong("exp") * 1000))
      }
    } catch (exception: Exception) {
      Log.e("JwtDecoder", exception.message)
    }
    throw InvalidTokenException()
  }

  class InvalidTokenException : Exception("Invalid Token")
}