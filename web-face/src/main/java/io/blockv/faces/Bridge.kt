package io.blockv.faces

import io.blockv.common.model.Vatom
import org.json.JSONObject

interface Bridge {

  fun onMessage(message: Message)

  fun onVatomUpdate(vatom: Vatom)

  class Message(
    val version: String,
    val source: String,
    val name: String,
    val requestId: String?,
    val payload: JSONObject?
  ) {

    constructor(data: JSONObject) : this(
      data.optString("version", "1.0.0"),
      data.getString("source"),
      data.getString("name"),
      data.optString(if (data.has("responseID")) "responseID" else "request_id", null),
      data.optJSONObject(if (data.has("data")) "data" else "payload")
    )
  }
}