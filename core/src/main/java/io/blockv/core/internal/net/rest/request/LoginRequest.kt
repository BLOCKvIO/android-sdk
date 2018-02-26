package io.blockv.core.internal.net.rest.request

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/21.
 */

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