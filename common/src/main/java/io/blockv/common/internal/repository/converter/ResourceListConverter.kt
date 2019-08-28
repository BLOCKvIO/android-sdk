package io.blockv.common.internal.repository.converter

import androidx.room.TypeConverter
import io.blockv.common.model.Resource
import org.json.JSONArray
import org.json.JSONObject

object ResourceListConverter {

  @TypeConverter
  @JvmStatic
  fun toList(data: String?): List<Resource> {
    try {
      val json = JSONArray(data)
      val array = ArrayList<Resource>()
      (0 until json.length()).forEach {
        val obj = json.getJSONObject(it)
        array.add(
          Resource(
            obj.optString("name", ""),
            obj.optString("type", ""),
            obj.optString("url", "")
          )
        )
      }
      return array
    } catch (e: Exception) {
    }
    return emptyList()
  }

  @TypeConverter
  @JvmStatic
  fun toString(data: List<Resource>?): String {
    val array = JSONArray()
    data?.forEach {
      val obj = JSONObject()
      obj.put("name", it.name)
      obj.put("type", it.type)
      obj.put("url", it.url)
      array.put(obj)
    }
    return array.toString()
  }
}