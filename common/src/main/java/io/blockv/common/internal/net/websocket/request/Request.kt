package io.blockv.common.internal.net.websocket.request

import org.json.JSONObject

interface Request {

  fun toJson(): JSONObject
}