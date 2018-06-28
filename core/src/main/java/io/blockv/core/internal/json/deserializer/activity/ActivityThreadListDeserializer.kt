package io.blockv.core.internal.json.deserializer.activity

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.activity.ActivityThread
import io.blockv.core.model.activity.ActivityThreadList
import org.json.JSONObject

class ActivityThreadListDeserializer(val threadDeserializer: Deserializer<ActivityThread>) : Deserializer<ActivityThreadList> {
  override fun deserialize(data: JSONObject): ActivityThreadList? {
    try {
      val cursor: String = data.getString("cursor")
      val threads = data.getJSONArray("threads")
      val threadArray: ArrayList<ActivityThread> = ArrayList()
      (0 until threads.length()).forEach {
        val thread = threads.getJSONObject(it)
        if (thread != null) {
          val out = threadDeserializer.deserialize(thread)
          if (out != null) {
            threadArray.add(out)
          }
        }
      }
      return ActivityThreadList(cursor, threadArray)
    } catch (e: Exception) {
      android.util.Log.w("Deserializer", "ActivityThreadListDeserializer - " + e.message)
    }
    return null
  }
}