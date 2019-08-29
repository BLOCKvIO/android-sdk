package io.blockv.common.internal.repository.converter

import androidx.room.TypeConverter
import io.blockv.common.model.ChildPolicy
import io.blockv.common.model.CreationPolicy
import org.json.JSONArray
import org.json.JSONObject

object ChildPolicyListConverter {

  @TypeConverter
  @JvmStatic
  fun toList(data: String?): List<ChildPolicy> {
    try {
      val json = JSONArray(data)
      val array = ArrayList<ChildPolicy>()
      (0 until json.length()).forEach {
        val jsonObject = json.getJSONObject(it)
        array.add(
          ChildPolicy(
            jsonObject.optInt("count", 0),
            jsonObject.optString("templateVariation"),
            CreationPolicy(
              jsonObject.optString("autoCreate"),
              jsonObject.optInt("autoCreateCount"),
              jsonObject.optBoolean("isAutoCreateCountRandom"),
              jsonObject.optBoolean("isEnforcePolicyCountMax"),
              jsonObject.optBoolean("isEnforcePolicyCountMin"),
              jsonObject.optInt("policyCountMax"),
              jsonObject.optInt("policyCountMin")
            )
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
  fun toString(data: List<ChildPolicy>?): String {
    val array = JSONArray()
    data?.forEach {
      array.put(
        JSONObject()
          .put("count", it.count)
          .put("templateVariation", it.templateVariation)
          .put("autoCreate", it.creationPolicy?.autoCreate)
          .put("autoCreateCount", it.creationPolicy?.autoCreateCount)
          .put("isAutoCreateCountRandom", it.creationPolicy?.isAutoCreateCountRandom)
          .put("isEnforcePolicyCountMax", it.creationPolicy?.isEnforcePolicyCountMax)
          .put("isEnforcePolicyCountMin", it.creationPolicy?.isEnforcePolicyCountMin)
          .put("policyCountMax", it.creationPolicy?.policyCountMax)
          .put("policyCountMin", it.creationPolicy?.policyCountMin)
      )
    }
    return array.toString()
  }
}