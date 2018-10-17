package io.blockv.common

import io.blockv.common.util.JsonUtil
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test


class JsonUnitTest {

  @Test
  fun primitiveAddTest() {
    val left = JSONObject().put("a", "A")
    val right = JSONObject().put("b", "B")
    val expected = JSONObject()
      .put("a", "A")
      .put("b", "B")
    JsonUtil.merge(left, right)

    Assert.assertEquals(expected.toString(), left.toString())
  }

  @Test
  fun primitiveReplaceTest() {
    val left = JSONObject().put("a", "A")
    val right = JSONObject().put("a", "B")
    val expected = JSONObject()
      .put("a", "B")
    JsonUtil.merge(left, right)

    Assert.assertEquals(expected.toString(), left.toString())
  }


  @Test
  fun arrayReplaceTest() {
    val left = JSONObject().put(
      "a", JSONArray()
        .put("hello")
        .put("world")
    )
    val right = JSONObject().put(
      "a", JSONArray()
        .put("goodbye")
    )
    val expected = JSONObject()
      .put("a", JSONArray().put("goodbye"))
    JsonUtil.merge(left, right)

    Assert.assertEquals(expected.toString(), left.toString())
  }


  @Test
  fun objectReplaceTest() {
    val left = JSONObject().put("a", JSONObject().put("a", "A"))

    val right = JSONObject().put(
      "a", JSONObject()
        .put("a", "B")
    )
    val expected = JSONObject()
      .put("a", JSONObject().put("a", "B"))
    JsonUtil.merge(left, right)

    Assert.assertEquals(expected.toString(), left.toString())
  }

  @Test
  fun objectMergeTest() {
    val left = JSONObject().put("a", JSONObject().put("a", "A"))

    val right = JSONObject().put(
      "a", JSONObject()
        .put("b", "B")
    )
    val expected = JSONObject()
      .put("a", JSONObject().put("a", "A").put("b", "B"))
    JsonUtil.merge(left, right)

    Assert.assertEquals(expected.toString(), left.toString())
  }

  @Test
  fun objectNullMergeTest() {
    val left = JSONObject().put("a", JSONObject().put("a", "A"))

    val right = JSONObject().put(
      "a", JSONObject.NULL
    )
    val expected = JSONObject()
      .put("a", JSONObject.NULL)

    JsonUtil.merge(left, right)

    Assert.assertEquals(expected.toString(), left.toString())
  }

}