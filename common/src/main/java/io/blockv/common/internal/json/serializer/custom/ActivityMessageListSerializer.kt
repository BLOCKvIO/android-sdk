package io.blockv.common.internal.json.serializer.custom

import android.util.Log
import io.blockv.common.internal.json.serializer.Serializer
import io.blockv.common.model.ActivityMessage
import io.blockv.common.model.ActivityMessageList
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KClass

class ActivityMessageListSerializer : Serializer<ActivityMessageList> {

  override fun serialize(data: ActivityMessageList, serializers: Map<KClass<*>, Serializer<Any>>): JSONObject? {
    val out = JSONObject()
    out.put("cursor", data.cursor)
    val messages = JSONArray()
    data.messages.forEach {
      messages.put(
        JSONObject().put(
          "message",
          serializers
            .getValue(Any::class)
            .serialize(it, serializers)
        )
      )
    }
    out.put("messages", messages)
    return out
  }

  override fun deserialize(
    type: KClass<*>,
    data: JSONObject,
    serializers: Map<KClass<*>, Serializer<Any>>
  ): ActivityMessageList? {

    val jsonMessages = data.getJSONArray("messages") ?: JSONArray()
    val messages = ArrayList<ActivityMessage>()

    (0 until jsonMessages.length()).forEach {
      val message = serializers
        .getValue(Any::class).deserialize(
          ActivityMessage::class,
          jsonMessages.getJSONObject(it)?.getJSONObject("message") ?: JSONObject(),
          serializers
        )
      if (message != null) {
        messages.add(message as ActivityMessage)
      }
    }
    return ActivityMessageList(data.getString("cursor") ?: "", messages)
  }
}