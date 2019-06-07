package io.blockv.common.internal.net.rest.request

import org.json.JSONObject

class PushTokenRequest(val token: String, val enabled: Boolean, val platform: String = "android") {

  fun toJson(): JSONObject {
    return JSONObject()
      .put("fcm_token", token)
      .put("platform_id", platform)
      .put("on", enabled)
  }
}