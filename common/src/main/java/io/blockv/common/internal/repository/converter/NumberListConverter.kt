package io.blockv.common.internal.repository.converter

import androidx.room.TypeConverter

object NumberListConverter {

  @TypeConverter
  @JvmStatic
  fun toFloatList(data: String?): List<Float> {
    return data?.split(";")?.toList()?.map {
      it.toFloatOrNull()
    }?.filterNotNull() ?: emptyList()
  }

  @TypeConverter
  @JvmStatic
  fun toString(data: List<Float>?): String {
    var outData = ""
    data?.forEach {
      outData += "$it;"
    }
    return outData
  }
}