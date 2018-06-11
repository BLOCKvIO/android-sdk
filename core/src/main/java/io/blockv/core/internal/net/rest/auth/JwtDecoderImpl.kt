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
        return DecodedJwt(jwt, data.getString("user_id"), Date(data.getLong("exp")*1000))
      }
    } catch (exception: Exception) {
      Log.e("JwtDecoder",exception.message)
    }
    throw InvalidTokenException()
  }

  class InvalidTokenException : Exception("Invalid Token")
}