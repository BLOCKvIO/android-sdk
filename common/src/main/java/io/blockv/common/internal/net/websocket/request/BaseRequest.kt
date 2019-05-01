package io.blockv.common.internal.net.websocket.request

import org.json.JSONObject

class BaseRequest(
  val command: String,
  val id: String = "1",
  val version: String = "1",
  val type: String = "command",
  val payload: Request
) : Request {

  override fun toJson(): JSONObject {
    return JSONObject()
      .put("cmd", command)
      .put("id", id)
      .put("version", version)
      .put("type", type)
      .put("payload", payload.toJson())
  }
}