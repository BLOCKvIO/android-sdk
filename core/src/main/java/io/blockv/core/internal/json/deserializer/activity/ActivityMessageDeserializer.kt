package io.blockv.core.internal.json.deserializer.activity

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.ActivityMessage
import io.blockv.core.model.Resource
import org.json.JSONArray
import org.json.JSONObject

class ActivityMessageDeserializer : Deserializer<ActivityMessage?> {

  override fun deserialize(data: JSONObject): ActivityMessage? {
    try {
      val id = data.getLong("msg_id")
      val userId = data.getString("user_id")
      val vatomIds = data.optJSONArray("vatoms") ?: JSONArray()
      val tempVariations = data.optJSONArray("templ_vars") ?: JSONArray()
      val message = data.getString("msg")
      val action = data.getString("action_name")
      val whenCreated = data.getString("when_created")
      val triggeredBy = data.getString("triggered_by")
      val resources = data.optJSONArray("generic") ?: JSONArray()
      val geoPos = data.optJSONArray("geo_pos") ?: JSONArray()

      val vatomsArray = ArrayList<String>()
      (0 until vatomIds.length()).forEach({ vatomsArray.add(vatomIds.getString(it)) })
      val tempVarArray = ArrayList<String>()
      (0 until tempVariations.length()).forEach({ tempVarArray.add(tempVariations.getString(it)) })
      val resourceArray = ArrayList<Resource>()
      (0 until resources.length()).forEach({
        val resource = resources.getJSONObject(it)
        if (resource != null) {
          resourceArray.add(Resource(
            resource.optString("name"),
            resource.optString("resourceType"),
            resource.optJSONObject("value").optString("value")
          ))
        }
      })
      val geoPosArray = ArrayList<Double>()
      (0 until geoPos.length()).forEach({ geoPosArray.add(geoPos.getDouble(it)) })

      return ActivityMessage(
        id,
        userId,
        vatomsArray,
        tempVarArray,
        message,
        action,
        whenCreated,
        triggeredBy,
        resourceArray,
        geoPosArray)

    } catch (e: Exception) {
      android.util.Log.w("Deserializer", "ActivityMessageDeserializer - " + e.message)
    }
    return null
  }
}