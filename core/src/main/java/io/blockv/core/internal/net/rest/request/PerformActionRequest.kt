package io.blockv.core.internal.net.rest.request

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/27.
 */
class PerformActionRequest(val action: String, val id: String, val payload: JSONObject?) {

  fun toJson(): JSONObject {
    val data: JSONObject = payload ?: JSONObject()
    data.put("this.id", id)
    return data
  }
}