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
package io.blockv.common.internal.json.serializer.user

import io.blockv.common.internal.json.serializer.Serializer
import io.blockv.common.model.Jwt
import org.json.JSONObject
import kotlin.reflect.KClass

class JwtSerializer : Serializer<Jwt?> {
  override fun serialize(data: Jwt?, serializers: Map<KClass<*>, Serializer<Any>>): JSONObject {
    val out = JSONObject()
    if (data != null) {
      out.put("token", data.token)
      out.put("token_type", data.type)
    }
    return out
  }
}