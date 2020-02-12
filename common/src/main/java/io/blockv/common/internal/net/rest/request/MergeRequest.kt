package io.blockv.common.internal.net.rest.request

import org.json.JSONObject

class MergeRequest(
  var token: String,
  var tokenType: String,
  var password: String
) {

  fun toJson(): JSONObject {
    val out = JSONObject()
    out.put("token", token)
    out.put("token_type", tokenType)
    out.put("auth_data", JSONObject().put("password", password))
    return out
  }
}