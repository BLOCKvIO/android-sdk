package io.blockv.common.internal.json.serializer

import io.blockv.common.internal.json.JsonModule
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

open class GenericSerializer : Serializer<Any> {

  companion object {
    val booleanClass = Boolean::class
    val stringClass = String::class
    val intClass = Int::class
    val floatClass = Float::class
    val doubleClass = Double::class
    val longClass = Long::class
    val listClass = List::class
    val mapClass = Map::class
    val setClass = Set::class
    val jsonObjectClass = JSONObject::class
    val jsonArrayClass = JSONArray::class
  }

  fun getAsJsonValue(fieldType: KClass<*>, value: Any?, serializers: Map<KClass<*>, Serializer<Any>>): Any {
    val nullCheck = { it: Any? ->
      it ?: JSONObject.NULL
    }

    if (value == null) {
      return JSONObject.NULL
    }
    if (serializers[fieldType] != null) {
      return serializers[fieldType]!!.serialize(value, serializers) as Any
    }

    return when {
      booleanClass.isSubclassOf(fieldType) -> nullCheck(value as Boolean)
      stringClass.isSubclassOf(fieldType) -> nullCheck(value as String)
      intClass.isSubclassOf(fieldType) -> nullCheck(value as Int)
      floatClass.isSubclassOf(fieldType) -> nullCheck(value as Float)
      doubleClass.isSubclassOf(fieldType) -> nullCheck(value as Double)
      longClass.isSubclassOf(fieldType) -> nullCheck(value as Long)
      jsonObjectClass.isSubclassOf(fieldType) -> nullCheck(value as JSONObject)
      jsonArrayClass.isSubclassOf(fieldType) -> nullCheck(value as JSONArray)
      listClass.isSubclassOf(fieldType) -> {
        val list = value as List<Any?>
        val outList = JSONArray()
        list.forEach {
          if (it !== null) {
            val data = getAsJsonValue(it::class, it, serializers)
            if (data != JSONObject.NULL) {
              outList.put(data)
            }
          }
        }

        outList

      }
      setClass.isSubclassOf(fieldType) -> {
        val set = value as Set<Any?>
        val outList = JSONArray()
        set.forEach {
          if (it !== null) {
            val data = getAsJsonValue(it::class, it, serializers)
            if (data != JSONObject.NULL) {
              outList.put(data)
            }
          }
        }
        outList

      }
      mapClass.isSubclassOf(fieldType) -> {

        val map = value as Map<*, *>
        val outObject = JSONObject()
        map.keys.forEach {
          if (it != null && (it is String) && map[it] != null) {
            outObject.put(it.toString(), getAsJsonValue(map[it]!!::class, map[it], serializers))
          }
        }
        outObject

      }
      else -> {
        serialize(value, serializers) ?: JSONObject.NULL
      }
    }

  }

  fun getAsJsonValue(
    fieldType: KClass<*>,
    property: KProperty<*>,
    data: Any,
    serializers: Map<KClass<*>, Serializer<Any>>
  ): Any {
    return getAsJsonValue(fieldType, property.getter.call(data), serializers)
  }

  override fun serialize(data: Any, serializers: Map<KClass<*>, Serializer<Any>>): JSONObject? {
    val clss = data::class

    val out = JSONObject()
    for (it in clss.memberProperties) {
      val fieldType = it.returnType.classifier as KClass<*>

      val annotation: JsonModule.Serialize? = it.annotations.find { annotation ->
        annotation is JsonModule.Serialize
      } as JsonModule.Serialize

      if (annotation != null) {
        val name = if (annotation.name.isEmpty()) it.name else annotation.name

        val outPath = if (annotation.path.isNotEmpty()) {
          val path = annotation.path.split(".")

          var start = out

          path.forEach {
            if (!start.has(it)) {
              start.put(it, JSONObject())
            }
            start = start[it] as JSONObject
          }

          start
        } else
          out

        outPath.put(name, getAsJsonValue(fieldType, it, data, serializers))

      }
    }
    return out
  }

}