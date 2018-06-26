package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.ActivityEvent
import io.blockv.core.model.Resource
import org.json.JSONArray
import org.json.JSONObject

class ActivityEventDeserializer : Deserializer<ActivityEvent> {
  override fun deserialize(data: JSONObject): ActivityEvent? {
    try {
      val eventId = data.getLong("msg_id")
      val targetUserId = data.getString("user_id")
      val triggeredBy = data.getString("triggered_by")
      val vatomsIds = data.optJSONArray("vatoms") ?: JSONArray()
      val resources = data.optJSONArray("generic") ?: JSONArray()
      val message = data.getString("msg")
      val actionName = data.getString("action_name")
      val whenCreated = data.getString("when_created")

      val vatomsArray = ArrayList<String>()
      (0 until vatomsIds.length())
        .forEach {
          val vatom: String = vatomsIds.getString(it)
          vatomsArray.add(vatom)
        }

      val resourceArray: ArrayList<Resource> = ArrayList()
      (0 until resources.length())
        .forEach {
          val resource = resources.optJSONObject(it)
          if (resource != null) {
            resourceArray.add(Resource(
              resource.optString("name"),
              resource.optString("resourceType"),
              resource.optJSONObject("value").optString("value")
            ))
          }
        }

      return ActivityEvent(
        eventId,
        targetUserId,
        triggeredBy,
        vatomsArray,
        resourceArray,
        message,
        actionName,
        whenCreated)
    } catch (e: Exception) {
      android.util.Log.e("ActEventDeserializer", e.message)
    }
    return null
  }
}