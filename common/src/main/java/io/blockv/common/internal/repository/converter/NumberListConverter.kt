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
  fun toDoubleList(data: String?): List<Double> {
    return data?.split(";")?.toList()?.map {
      it.toDoubleOrNull()
    }?.filterNotNull() ?: emptyList()
  }

  @TypeConverter
  @JvmStatic
  fun floatToString(data: List<Float>?): String {
    var outData = ""
    data?.forEach {
      outData += "$it;"
    }
    return outData
  }

  @TypeConverter
  @JvmStatic
  fun doubleToString(data: List<Double>?): String {
    var outData = ""
    data?.forEach {
      outData += "$it;"
    }
    return outData
  }
}