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