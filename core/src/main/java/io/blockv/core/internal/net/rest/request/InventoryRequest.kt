package io.blockv.core.internal.net.rest.request

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/25.
 */
class InventoryRequest(val parentId: String, val pageToken: String?, val pageAmount: Int) {

  fun toJson(): JSONObject {
    val out: JSONObject = JSONObject()
    out.put("id", parentId)
    out.put("page_token", pageToken)
    out.put("page_amount", pageAmount)
    return out
  }
}