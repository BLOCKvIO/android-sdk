package io.blockv.core.internal.net.rest.request

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/21.
 */

class GuestLoginRequest(var token: String) {

  fun toJson(): JSONObject {
    val out = JSONObject()
    out.put("token", token)
    out.put("token_type", "guest_id")
    return out
  }
}