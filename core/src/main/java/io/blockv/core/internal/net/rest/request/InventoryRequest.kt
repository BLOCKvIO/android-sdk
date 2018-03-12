package io.blockv.core.internal.net.rest.request

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/25.
 */
class InventoryRequest(val parentId: String) {

  fun toJson(): JSONObject {
    val out = JSONObject()
    out.put("id", parentId)
    return out
  }
}