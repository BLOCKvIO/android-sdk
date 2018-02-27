package io.blockv.core.internal.net.rest.request

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/27.
 */
class VatomRequest(val ids: List<String>) {

  fun toJson(): JSONObject {
    val out = JSONObject()
    val idArray = JSONArray()
    (0..ids.size).forEach {
      idArray.put(ids[it])
    }
    out.put("ids", idArray)
    return out
  }
}