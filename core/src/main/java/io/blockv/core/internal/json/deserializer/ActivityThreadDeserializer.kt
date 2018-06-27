package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.ActivityMessage
import io.blockv.core.model.ActivityThread
import org.json.JSONObject

class ActivityThreadDeserializer(private val messageDeserializer: Deserializer<ActivityMessage>) : Deserializer<ActivityThread> {

  override fun deserialize(data: JSONObject): ActivityThread? {
    try {
      val id: String = data.getString("name")
      val whenModified = data.getLong("when_modified")
      val lastMessage = messageDeserializer.deserialize(data.getJSONObject("last_message"))
      val user = data.getJSONObject("last_message_user_info")
      val userInfo = ActivityThread.UserInfo(user.optString("name", ""), user.optString("avatar_uri", ""))
      return ActivityThread(id, whenModified, lastMessage!!, userInfo)
    } catch (e: Exception) {
      android.util.Log.w("Deserializer", "ActivityThreadDeserializer - " + e.message)
    }
    return null
  }

}