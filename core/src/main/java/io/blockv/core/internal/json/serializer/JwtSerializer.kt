package io.blockv.core.internal.json.serializer

import io.blockv.core.model.Jwt
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/24.
 */
class JwtSerializer : Serializer<Jwt?> {
  override fun serialize(data: Jwt?): JSONObject {
    val out = JSONObject()
    if (data != null) {
      out.put("token", data.token)
      out.put("token_type", data.type)
      out.put("expires_in", data.expiresIn)
    }
    return out
  }
}