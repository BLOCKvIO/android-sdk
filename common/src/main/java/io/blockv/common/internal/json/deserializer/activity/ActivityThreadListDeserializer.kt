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
package io.blockv.common.internal.json.deserializer.activity

import io.blockv.common.internal.json.deserializer.Deserializer
import io.blockv.common.model.ActivityThread
import io.blockv.common.model.ActivityThreadList
import org.json.JSONObject

class ActivityThreadListDeserializer(val threadDeserializer: Deserializer<ActivityThread>) :
  Deserializer<ActivityThreadList> {
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