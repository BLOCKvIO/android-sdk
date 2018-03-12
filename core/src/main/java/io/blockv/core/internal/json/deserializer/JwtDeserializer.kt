package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.Jwt
import java.util.*

class JwtDeserializer : Deserializer<Jwt> {
  override fun deserialize(data: org.json.JSONObject): Jwt? {
    try {
      val token: String = data.getString("token")
      val tokenType: String = data.getString("token_type")
      val expiresIn: Int = data.getInt("expires_in")
      val expires: Int = data.optInt("expires", (Date().time / 1000).toInt() + expiresIn)

      android.util.Log.e("JwtDeserializer", "" + token + " " + tokenType + " " + expiresIn);
      return Jwt(
        token,
        tokenType,
        expiresIn,
        expires)

    } catch (e: Exception) {
      android.util.Log.w("JwtDeserializer", e.message)
    }
    return null
  }

}