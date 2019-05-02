package io.blockv.common.internal.net.rest.request

import org.json.JSONObject

class TokenRequest(val appId: String, val code: String, val redirectUri: String) {

  fun toJson(): JSONObject {
    return JSONObject()
      .put("grant_type", "authorization_code")
      .put("client_id", appId)
      .put("code", code)
      .put("redirect_uri", redirectUri)
  }
}