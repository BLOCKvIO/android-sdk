/**
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.internal.json.deserializer.vatom

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.vatom.Action
import org.json.JSONObject

class ActionDeserializer : Deserializer<Action> {
  override fun deserialize(data: JSONObject): Action? {
    try {
      val name: String = data.getString("name")
      val parts: List<String> = name.split("::Action::")
      return Action(parts[0], parts[1])
    } catch (e: Exception) {
      android.util.Log.w("ActionDeserializer", e.message)
    }
    return null
  }

}