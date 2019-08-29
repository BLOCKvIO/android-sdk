package io.blockv.common.internal.repository.converter

import androidx.room.TypeConverter
import org.json.JSONObject

object JsonConverter {

  @TypeConverter
  @JvmStatic
  fun toJsonObject(json: String): JSONObject? {
    try {
      return JSONObject(json)
    } catch (e: Exception) {
    }
    return null
  }

  @TypeConverter
  @JvmStatic
  fun toJsonString(json: JSONObject?): String? {
    return json?.toString()
  }
}