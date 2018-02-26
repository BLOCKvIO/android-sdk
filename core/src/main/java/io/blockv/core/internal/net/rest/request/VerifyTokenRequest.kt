package io.blockv.android.core.internal.net.rest.request

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/21.
 */
class VerifyTokenRequest(var tokenType: String, var token: String, var code: String) {

  fun toJson(): JSONObject {
    val out: JSONObject = JSONObject()
    out.put("token", token)
    out.put("token_type", tokenType)
    out.put("verify_code", code)
    return out
  }
}