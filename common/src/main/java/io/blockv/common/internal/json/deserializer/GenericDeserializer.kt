package io.blockv.common.internal.json.deserializer

import android.annotation.SuppressLint
import android.util.Log
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.util.JsonUtil
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

class GenericDeserializer<T> : Deserializer<T>() {

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

  fun getConstructor(type: KClass<*>): KFunction<Any>? {

    return type.constructors.find {
      it.annotations.find { annotation ->
        annotation is JsonModule.Serializable
      } != null
    }
  }

  fun getJsonObjectForPath(data: JSONObject, path: String?): JSONObject? {
    if (path == null || path.isEmpty())
      return data

    val parts = path.split(".")

    var out = data
    for (key in parts) {
      if (!out.has(key) || out.isNull(key) || !JsonUtil.isJsonObject(out, key)) {
        return null
      }
      out = out.optJSONObject(key) ?: return null
    }
    return out
  }

  fun getValues(
    type: KClass<*>,
    data: JSONObject,
    deserializers: Map<KClass<*>, Deserializer<*>>
  ): Map<String, Any?> {
    val out = HashMap<String, Any?>()
    for (prop in type.memberProperties) {
      val annotation: JsonModule.Serialize? = prop.annotations.find {
        it is JsonModule.Serialize
      } as JsonModule.Serialize

      if (annotation != null) {
        val name = prop.name
        val fieldType: KClass<*> = prop.returnType.classifier as KClass<*>
        val json = getJsonObjectForPath(data, annotation.path)
        val jsonName = if (annotation.name.isNotEmpty()) annotation.name else name
        out[name] =
          if ((json == null || !json.has(jsonName) || json.isNull(jsonName))) {
            when {
              prop.returnType.isMarkedNullable -> null
              annotation.default -> when {
                booleanClass.isSubclassOf(fieldType) -> false
                stringClass.isSubclassOf(fieldType) -> ""
                intClass.isSubclassOf(fieldType) -> 0
                floatClass.isSubclassOf(fieldType) -> 0f
                doubleClass.isSubclassOf(fieldType) -> 0.0
                longClass.isSubclassOf(fieldType) -> 0L
                jsonObjectClass.isSubclassOf(fieldType) -> JSONObject()
                jsonArrayClass.isSubclassOf(fieldType) -> JSONArray()
                listClass.isSubclassOf(fieldType) -> ArrayList<Any>()
                mapClass.isSubclassOf(fieldType) -> HashMap<Any, Any>()
                setClass.isSubclassOf(fieldType) -> HashSet<Any>()
                else -> throw Exception("Deserialization Unknown type : " + type.simpleName + " - " + name)
              }
              else -> throw Exception("Deserialization key not found : " + type.simpleName + " - " + name)
            }
          } else {
            when {
              deserializers.containsKey(fieldType) -> deserializers[fieldType]!!.deserialize(
                fieldType,
                json.getJSONObject(jsonName),
                deserializers
              )
              booleanClass.isSubclassOf(fieldType) -> json.getBoolean(jsonName)
              stringClass.isSubclassOf(fieldType) -> json.getString(jsonName)
              intClass.isSubclassOf(fieldType) -> json.getInt(jsonName)
              floatClass.isSubclassOf(fieldType) -> json.getDouble(jsonName).toFloat()
              doubleClass.isSubclassOf(fieldType) -> json.getDouble(jsonName)
              longClass.isSubclassOf(fieldType) -> json.getDouble(jsonName).toLong()
              jsonObjectClass.isSubclassOf(fieldType) -> json.getJSONObject(jsonName)
              jsonArrayClass.isSubclassOf(fieldType) -> json.getJSONArray(jsonName)
              listClass.isSubclassOf(fieldType) -> {
                val jsonArray = json.getJSONArray(jsonName)
                val list = ArrayList<Any>()
                (0 until jsonArray.length()).forEach {
                  jsonArray.
                }

              }
              mapClass.isSubclassOf(fieldType) -> {

              }
              setClass.isSubclassOf(fieldType) -> {

              }
              else -> throw Exception("Deserialization Unknown type : " + type.simpleName + " - " + name)
            }
          }

        Log.e("deserilizer", name + " - " + prop.returnType)
      }

    }
    return out
  }


  override fun deserialize(
    type: KClass<*>,
    data: JSONObject,
    deserializers: Map<KClass<*>, Deserializer<*>>
  ): T? {

    getValues(type, data, deserializers)
    return null
  }

}