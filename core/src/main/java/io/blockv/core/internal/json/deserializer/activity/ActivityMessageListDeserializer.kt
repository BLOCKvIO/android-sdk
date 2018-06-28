package io.blockv.core.internal.json.deserializer.activity

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.ActivityMessage
import io.blockv.core.model.ActivityMessageList
import org.json.JSONArray
import org.json.JSONObject

class ActivityMessageListDeserializer(private val messageDeserializer: Deserializer<ActivityMessage?>) : Deserializer<ActivityMessageList> {

  override fun deserialize(data: JSONObject): ActivityMessageList? {
    try {
      val cursor = data.getString("cursor")
      val messages = data.optJSONArray("messages") ?: JSONArray()
      val messageArray: ArrayList<ActivityMessage> = ArrayList()
      (0 until messages.length()).forEach {
        val message = messages.getJSONObject(it)
        if (message != null) {
          val out = messageDeserializer.deserialize(message.optJSONObject("message") ?: JSONObject())
          if (out != null) {
            messageArray.add(out)
          }
        }
      }
      return ActivityMessageList(cursor, messageArray)
    } catch (e: Exception) {
      android.util.Log.w("Deserializer", "ActivityMessageListDeserializer - " + e.message)
    }
    return null
  }
}