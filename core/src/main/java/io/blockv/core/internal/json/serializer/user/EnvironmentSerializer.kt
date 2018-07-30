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
package io.blockv.core.internal.json.serializer.user

import io.blockv.core.internal.json.serializer.Serializer
import io.blockv.core.model.Environment
import org.json.JSONObject

class EnviromentSerializer : Serializer<Environment?> {
  override fun serialize(data: Environment?): JSONObject {
    val out: JSONObject = JSONObject()
    if (data != null) {
      out.put("app_id", data.appId)
      out.put("rest", data.rest)
      out.put("cdn", data.cdn)
      out.put("wss", data.wss)
    }
    return out
  }
}