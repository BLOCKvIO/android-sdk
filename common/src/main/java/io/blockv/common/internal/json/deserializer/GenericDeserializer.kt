package io.blockv.common.internal.json.deserializer

import io.blockv.common.internal.json.JsonModule
import io.blockv.common.util.JsonUtil
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.memberProperties

class GenericDeserializer<T> : Deserializer<T>() {

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
      val annotation = prop.annotations.find {
        it is JsonModule.Serialize
      }

      if (annotation != null) {
        annotation as JsonModule.Serialize
        val name = prop.name
        val fieldType: KClass<*> = prop.returnType.classifier as KClass<*>
        val json = getJsonObjectForPath(data, annotation.path)
        val jsonName = if (annotation.name.isNotEmpty()) annotation.name else name

        out[name] =
          if ((json == null || !json.has(jsonName) || json.isNull(jsonName))) {
            //handle null/missing json
            when {
              annotation.default -> when (fieldType) {
                Boolean::class -> false
                String::class -> ""
                Int::class -> 0
                Float::class -> 0f
                Double::class -> 0.0
                Long::class -> 0L
                JSONObject::class -> JSONObject()
                JSONArray::class -> JSONArray()
                List::class -> ArrayList<Any>()
                Map::class -> HashMap<Any, Any>()
                Set::class -> HashSet<Any>()
                else -> throw Exception("Deserialization Unknown type : " + type.simpleName + " - " + name)
              }
              prop.returnType.isMarkedNullable -> null
              else -> throw Exception("Deserialization key not found : " + type.simpleName + " - " + name)
            }
          } else {

            if (deserializers.containsKey(fieldType)) {
              deserializers[fieldType]!!.deserialize(
                fieldType,
                json.getJSONObject(jsonName),
                deserializers
              )
            } else
              when (fieldType) {

                Boolean::class -> json.getBoolean(jsonName)
                String::class -> json.getString(jsonName)
                Int::class -> json.getInt(jsonName)
                Float::class -> json.getDouble(jsonName).toFloat()
                Double::class -> json.getDouble(jsonName)
                Long::class -> json.getDouble(jsonName).toLong()
                JSONObject::class -> json.getJSONObject(jsonName)
                JSONArray::class -> json.getJSONArray(jsonName)
                List::class -> {
                  val jsonArray = json.getJSONArray(jsonName)
                  val list = ArrayList<Any>()
                  if (prop.returnType.arguments[0].type != null) {
                    val innerType = (prop.returnType.arguments[0].type)?.classifier as KClass<*>

                    val add = java.util.ArrayList::class.declaredFunctions.find {
                      it.name == "add"
                    }
                    (0 until jsonArray.length()).forEach {

                      add?.call(
                        list, when (innerType) {
                          Boolean::class -> jsonArray.getBoolean(it)
                          String::class -> jsonArray.getString(it)
                          Int::class -> jsonArray.getInt(it)
                          Float::class -> jsonArray.getDouble(it).toFloat()
                          Double::class -> jsonArray.getDouble(it)
                          Long::class -> jsonArray.getDouble(it).toLong()
                          JSONObject::class -> jsonArray.getJSONObject(it)
                          JSONArray::class -> jsonArray.getJSONArray(it)
                          else -> if (
                            jsonArray.optJSONObject(it) != null) {
                            deserialize(
                              innerType,
                              jsonArray.getJSONObject(it),
                              deserializers
                            )
                          } else
                            null
                        }
                      )
                    }
                  }
                  list
                }
                Map::class -> {
                  val jsonObject = json.getJSONObject(jsonName)

                  val map = HashMap<String, Any>()

                  if (prop.returnType.arguments[0].type != null && prop.returnType.arguments[1].type != null) {
                    val keyType = (prop.returnType.arguments[0].type)?.classifier as KClass<*>
                    val valueType = (prop.returnType.arguments[1].type) as KClass<*>

                    if (keyType != String::class) {
                      throw Exception("Map key type must be string ${keyType.simpleName} - $name")
                    }

                    val put = java.util.HashMap::class.declaredFunctions.find {
                      it.name == "put"
                    }

                    jsonObject.keys().forEach {
                      put?.call(
                        map, it, when (valueType) {
                          Boolean::class -> json.getBoolean(jsonName)
                          String::class -> json.getString(jsonName)
                          Int::class -> json.getInt(jsonName)
                          Float::class -> json.getDouble(jsonName).toFloat()
                          Double::class -> json.getDouble(jsonName)
                          Long::class -> json.getDouble(jsonName).toLong()
                          JSONObject::class -> json.getJSONObject(jsonName)
                          JSONArray::class -> json.getJSONArray(jsonName)
                          else -> if (
                            JsonUtil.isJsonObject(json, jsonName)) {
                            deserialize(
                              valueType,
                              json.getJSONObject(jsonName),
                              deserializers
                            )
                          } else
                            null
                        }
                      )
                    }

                  }
                  map
                }
                Set::class -> {
                  val jsonArray = json.getJSONArray(jsonName)

                  val list = HashSet<Any>()

                  if (list != null && prop.returnType.arguments[0].type != null) {
                    val innerType = (prop.returnType.arguments[0].type)?.classifier as KClass<*>

                    val add = java.util.HashSet::class.declaredFunctions.find {
                      it.name == "add"
                    }
                    (0 until jsonArray.length()).forEach {

                      add?.call(
                        list, when (innerType) {
                          Boolean::class -> jsonArray.getBoolean(it)
                          String::class -> jsonArray.getString(it)
                          Int::class -> jsonArray.getInt(it)
                          Float::class -> jsonArray.getDouble(it).toFloat()
                          Double::class -> jsonArray.getDouble(it)
                          Long::class -> jsonArray.getDouble(it).toLong()
                          JSONObject::class -> jsonArray.getJSONObject(it)
                          JSONArray::class -> jsonArray.getJSONArray(it)
                          else -> if (
                            jsonArray.optJSONObject(it) != null) {
                            deserialize(
                              innerType,
                              jsonArray.getJSONObject(it),
                              deserializers
                            )
                          } else
                            null
                        }
                      )
                    }
                  }
                  list
                }
                else -> {
                  deserialize(fieldType, json.getJSONObject(jsonName), deserializers)
                }

              }
          }
      }

    }
    return out
  }


  override fun deserialize(
    type: KClass<*>,
    data: JSONObject,
    deserializers: Map<KClass<*>, Deserializer<*>>
  ): T? {

    val values = getValues(type, data, deserializers)

    val constructor = type.constructors.find { constructor ->
      constructor.annotations.find {
        it is JsonModule.Serializable
      } != null
    }

    val instance = constructor?.call(
      *(constructor.parameters.map { values[it.name] }.toTypedArray())
    ) ?: type.constructors.find {
      it.parameters.isEmpty()
    }?.call() ?: throw Exception("No serializable constructor found -" + type.simpleName)

    type.memberProperties.forEach { prop ->

      val annotation = prop.annotations.find {
        it is JsonModule.Serialize
      }

      if (annotation != null && !prop.isConst && prop is KMutableProperty<*>) {

        prop.setter.call(instance, values[prop.name])
      }
    }

    return instance as T
  }

}