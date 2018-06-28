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
package io.blockv.core.internal.json.deserializer.activity

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.ActivityMessage
import io.blockv.core.model.ActivityThread
import org.json.JSONObject

class ActivityThreadDeserializer(private val messageDeserializer: Deserializer<ActivityMessage?>) : Deserializer<ActivityThread> {

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