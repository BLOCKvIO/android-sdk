package io.blockv.common.internal.json
import io.blockv.common.internal.json.serializer.custom.ActionSerializer
import io.blockv.common.internal.json.serializer.GenericSerializer
import io.blockv.common.internal.json.serializer.Serializer
import io.blockv.common.model.*
import org.json.JSONObject
import kotlin.reflect.KClass

class JsonModule {

  val TAG: String = "JsonModule"

  val serializers: HashMap<KClass<*>, Serializer<Any>> = HashMap()

  val genericSerializer: Serializer<Any> = GenericSerializer()

  init {
    registerSerializer<Action>(ActionSerializer())
  }


  inline fun <reified T : Any> registerSerializer(serializer: Serializer<*>) {
    val key = T::class
    serializers[key] = serializer as Serializer<Any>
  }

  inline fun <reified T : Any> deserialize(json: JSONObject): T? {
    val key = T::class
    val serializer = serializers[key]
    return (serializer ?: genericSerializer).deserialize(key, json, serializers) as T
  }

  fun <T : Any> serialize(data: T): JSONObject? {

    val serializer = serializers[data::class]

    return (serializer ?: genericSerializer).serialize(data, serializers)

  }

}