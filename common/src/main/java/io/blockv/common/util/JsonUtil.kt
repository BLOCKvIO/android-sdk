package io.blockv.common.util

import org.json.JSONObject

/**
 * Class containing util functions to make working with JSONObjects easier.
 */
class JsonUtil {

  companion object {

    /**
     * Check if the value for the provided key is a JSONObject.
     *
     * @param data
     * @param key
     *
     * @return Boolean
     */
    fun isJsonObject(data: JSONObject, key: String): Boolean {
      return try {
        data.getJSONObject(key)
        true
      } catch (e: Exception) {
        false
      }
    }

    /**
     * Check if the value for the provided key is a JSONArray.
     *
     * @param data
     * @param key
     *
     * @return Boolean
     */
    fun isJsonArray(data: JSONObject, key: String): Boolean {
      return try {
        data.getJSONArray(key)
        true
      } catch (e: Exception) {
        false
      }
    }

    /**
     * Check if the value for the provided key is a primitive.
     *
     * @param data
     * @param key
     *
     * @return Boolean
     */
    fun isPrimitive(data: JSONObject, key: String): Boolean {
      return !isJsonObject(data, key) && !isJsonArray(data, key)
    }

    /**
     *  Add and replace values from right into left.
     *
     *  @param left
     *  @param right
     *
     */
    fun merge(left: JSONObject, right: JSONObject) {
      right.keys().forEach {
        if (!left.has(it)
          || !isJsonObject(left, it)
          || (isJsonObject(left, it) && !isJsonObject(right, it))
        ) {
          left.put(it, right[it])
        } else {
          merge(left.getJSONObject(it), right.getJSONObject(it))
        }
      }
    }
  }
}