package io.blockv.common.internal.repository.converter

import androidx.room.TypeConverter
import org.json.JSONArray

object StringListConverter {

  @TypeConverter
  @JvmStatic
  fun toList(data: String?): List<String> {
    try {
      val json = JSONArray(data)
      val array = ArrayList<String>()
      (0 until json.length()).forEach {
        array.add(json.getString(it))
      }
      return array
    } catch (e: Exception) {
    }
    return emptyList()
  }

  @TypeConverter
  @JvmStatic
  fun toString(data: List<String>?): String {
    val array = JSONArray()
    data?.forEach {
      array.put(it)
    }
    return array.toString()
  }
}