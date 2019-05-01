package io.blockv.common.internal.net.websocket.request

import org.json.JSONObject

class MonitorRequest(
  val bottomLeftLat: Double,
  val bottomLeftLon: Double,
  val topRightLat: Double,
  val topRightLon: Double
) : Request {
  override fun toJson(): JSONObject {
    return JSONObject()
      .put(
        "top_left", JSONObject()
          .put("lat", topRightLat)
          .put("lon", bottomLeftLon)
      )
      .put(
        "bottom_right", JSONObject()
          .put("lat", bottomLeftLat)
          .put("lon", topRightLon)
      )

  }
}