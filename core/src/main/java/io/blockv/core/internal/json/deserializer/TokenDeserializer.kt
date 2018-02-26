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