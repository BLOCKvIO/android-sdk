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
package io.blockv.common.internal.json.serializer

import org.json.JSONObject
import kotlin.reflect.KClass

interface Serializer<T> {

  fun serialize(
    data: T,
    serializers: Map<KClass<*>, Serializer<Any>>
  ): JSONObject?


  fun deserialize(
    type: KClass<*>,
    data: JSONObject,
    serializers: Map<KClass<*>, Serializer<Any>>
  ): T?

  annotation class Serialize(val name: String = "", val path: String = "", val default: Boolean = true)
  annotation class Serializable

}