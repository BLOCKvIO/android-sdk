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
package io.blockv.common.internal.json

import io.blockv.common.internal.json.serializer.GenericSerializer
import io.blockv.common.internal.json.serializer.Serializer
import io.blockv.common.internal.json.serializer.custom.ActionSerializer
import io.blockv.common.internal.json.serializer.custom.ActivityMessageListSerializer
import io.blockv.common.model.Action
import io.blockv.common.model.ActivityMessageList
import io.blockv.common.model.Model
import org.json.JSONObject
import kotlin.reflect.KClass

class JsonModule {

  val TAG: String = "JsonModule"

  val serializers: HashMap<KClass<*>, Serializer<Any>> = HashMap()

  val genericSerializer: Serializer<Any> = GenericSerializer()

  init {
    serializers[Any::class] = genericSerializer
    registerSerializer<ActivityMessageList>(ActivityMessageListSerializer())
    registerSerializer<Action>(ActionSerializer())
  }

  inline fun <reified T : Model> registerSerializer(serializer: Serializer<*>) {
    val key = T::class
    serializers[key] = serializer as Serializer<Any>
  }

  inline fun <reified T : Model> deserialize(json: JSONObject): T {
    val key = T::class
    val serializer = serializers[key]
    return (serializer ?: genericSerializer).deserialize(key, json, serializers) as T
  }

  fun <T : Model> deserialize(kclass: KClass<T>, json: JSONObject): T? {
    val serializer = serializers[kclass]
    return (serializer ?: genericSerializer).deserialize(kclass, json, serializers) as T
  }

  fun <T : Model> serialize(data: T): JSONObject? {

    val serializer = serializers[data::class]

    return (serializer ?: genericSerializer).serialize(data, serializers)

  }

}