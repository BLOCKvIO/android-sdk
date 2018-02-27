package io.blockv.core.internal.net.rest.request

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/26.
 */
class GeoRequest(val latitude: Double, val longitude: Double, val radius: Int, val limit: Int) {

  fun toJson(): JSONObject {
    val out = JSONObject()
    out.put("unit", "m")
    out.put("radius",radius)
    out.put("limit",limit)
    out.put("center_geo_pos",JSONObject().put("Lat",latitude).put("Lon",longitude))

    return out
  }
}