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
package io.blockv.common.internal.json.deserializer

import io.blockv.common.model.Jwt
import org.json.JSONObject
import kotlin.reflect.KClass

class JwtDeserializer : Deserializer<Jwt>() {
  override fun deserialize(
    type: KClass<*>,
    data: JSONObject,
    deserializers: Map<KClass<*>, Deserializer<*>>
  ): Jwt? {
    try {
      val token: String = data.getString("token")
      val tokenType: String = data.getString("token_type")

      return Jwt(
        token,
        tokenType
      )

    } catch (e: Exception) {
      android.util.Log.w("JwtDeserializer", e.message)
    }
    return null
  }

}