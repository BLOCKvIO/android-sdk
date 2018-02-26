package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.Environment
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/24.
 */
class EnvironmentDeserialzier : Deserializer<Environment> {
  override fun deserialize(data: JSONObject): Environment? {
    try {
      return Environment(data.getString("rest"),data.getString("app_id"))
    } catch (e: Exception) {
      android.util.Log.w("EnvironmentDeserialzier", e.message)
    }
    return null
  }
}