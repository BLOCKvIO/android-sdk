package io.blockv.core.internal.json.serializer

import io.blockv.core.model.Environment
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/24.
 */
class EnviromentSerializer : Serializer<Environment?> {
  override fun serialize(data: Environment?): JSONObject {
    val out: JSONObject = JSONObject()
    if(data!=null) {
      out.put("app_id", data.appId)
      out.put("rest", data.rest)
    }
    return out
  }
}