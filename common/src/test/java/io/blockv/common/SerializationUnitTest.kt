package io.blockv.common

import android.util.Log
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.model.*
import io.blockv.common.util.JsonUtil
import org.json.JSONObject
import org.junit.Test


class SerializationUnitTest {

  val jsonModule = JsonModule()

  fun compare(expected: JSONObject, actual: JSONObject) {
    expected.keys().forEach { key ->
      key as String
      if (actual.has(key)) {
        if (JsonUtil.isJsonObject(expected, key)) {
          compare(expected.getJSONObject(key), actual.getJSONObject(key))
        } else
          if (JsonUtil.isJsonArray(expected, key)) {
            val expectedArray = expected
              .getJSONArray(key)
            val actualArray = actual
              .getJSONArray(key)

            (0 until expectedArray.length()).forEach {
              if (actualArray[it] is JSONObject) {
                compare(expectedArray.getJSONObject(it), actualArray.getJSONObject(it))
              } else
                if (expectedArray.optDouble(it) != actualArray.optDouble(it) &&
                  expectedArray.optBoolean(it) != actualArray.optBoolean(it) &&
                  expectedArray.optString(it) != actualArray.optString(it)
                ) {
                  throw Exception("array values don't match key - $key index - $it expected - ${expectedArray[it]} actual - ${actualArray[it]}")
                }
            }

          } else
            if (expected.optDouble(key) != actual.optDouble(key) &&
              expected.optBoolean(key) != actual.optBoolean(key) &&
              expected.optString(key) != actual.optString(key)
            ) {
              throw Exception("values don't match key - $key expected - ${expected.get(key)} actual - ${actual.get(key)}")
            }
      } else {
        Log.e("actual", actual.toString())
        throw Exception("Missing key in actual - $key")
      }
    }

    actual.keys().forEach { key ->
      key as String
      if (!expected.has(key)) {
        throw Exception("Extra key in actual - $key")
      }
    }
  }

  @Test
  fun actionSerilizationTest() {
    val expected = JSONObject(MockData.action)
    val actual = jsonModule.serialize(jsonModule.deserialize<Action>(expected)!!)!!
    compare(expected, actual)
  }

  @Test
  fun faceSerilizationTest() {
    val expected = JSONObject(MockData.face)
    val actual = jsonModule.serialize(jsonModule.deserialize<Face>(expected)!!)!!
    compare(expected, actual)
  }

  @Test
  fun vatomSerilizationTest() {
    val expected = JSONObject(MockData.vatom)
    val actual = jsonModule.serialize(jsonModule.deserialize<Vatom>(expected)!!)!!
    compare(expected, actual)
  }

  @Test
  fun userSerilizationTest() {
    val expected = JSONObject(MockData.user)
    val actual = jsonModule.serialize(jsonModule.deserialize<User>(expected)!!)!!
    compare(expected, actual)
  }

  @Test
  fun assetProviderSerilizationTest() {
    val expected = JSONObject(MockData.assetProvider)
    val actual = jsonModule.serialize(jsonModule.deserialize<AssetProvider>(expected)!!)!!
    compare(expected, actual)
  }

  @Test
  fun jwtSerilizationTest() {
    val expected = JSONObject(MockData.jwt)
    val actual = jsonModule.serialize(jsonModule.deserialize<Jwt>(expected)!!)!!
    compare(expected, actual)
  }

  @Test
  fun tokenSerilizationTest() {
    val expected = JSONObject(MockData.token)
    val actual = jsonModule.serialize(jsonModule.deserialize<Token>(expected)!!)!!
    compare(expected, actual)
  }

  @Test
  fun activityThreadListSerilizationTest() {
    val expected = JSONObject(MockData.activityThreadList)
    val actual = jsonModule.serialize(jsonModule.deserialize<ActivityThreadList>(expected)!!)!!
    compare(expected, actual)
  }

  @Test
  fun activityMessageSerilizationTest() {
    val expected = JSONObject(MockData.activityMessage)
    val actual = jsonModule.serialize(jsonModule.deserialize<ActivityMessage>(expected)!!)!!
    compare(expected, actual)
  }
}