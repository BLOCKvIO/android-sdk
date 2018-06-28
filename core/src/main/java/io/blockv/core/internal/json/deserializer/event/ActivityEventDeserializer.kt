/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.internal.json.deserializer.event

import io.blockv.core.internal.json.deserializer.Deserializer
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
      val vatomIds = data.optJSONArray("vatoms") ?: JSONArray()
      val resources = data.optJSONArray("generic") ?: JSONArray()
      val message = data.getString("msg")
      val actionName = data.getString("action_name")
      val whenCreated = data.getString("when_created")

      val vatomsArray = ArrayList<String>()
      (0 until vatomIds.length())
        .forEach {
          val vatom: String = vatomIds.getString(it)
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