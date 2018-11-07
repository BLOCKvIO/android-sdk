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