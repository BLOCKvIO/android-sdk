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

import android.util.Log
import io.blockv.common.util.JsonUtil
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.memberProperties

class GenericSerializer : Serializer<Any> {

  override fun serialize(data: Any, serializers: Map<KClass<*>, Serializer<Any>>): JSONObject? {
    val clss = data::class

    val out = JSONObject()
    for (it in clss.memberProperties) {
      val fieldType = it.returnType.classifier as KClass<*>

      val annotation: Annotation? = it.annotations.find { annotation ->
        annotation is Serializer.Serialize
      }

      if (annotation != null) {
        annotation as Serializer.Serialize

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

  override fun deserialize(type: KClass<*>, data: JSONObject, serializers: Map<KClass<*>, Serializer<Any>>): Any? {

    if (serializers.containsKey(type)) {
      return serializers[type]!!.deserialize(type, data, serializers)
    }

    val values = getValues(type, data, serializers)

    val constructor = type.constructors.find { constructor ->
      constructor.annotations.find {
        it is Serializer.Serializable
      } != null
    }

    val instance = constructor?.call(
      *(constructor.parameters.map { values[it.name] }.toTypedArray())
    ) ?: type.constructors.find {
      it.parameters.isEmpty()
    }?.call() ?: throw Exception("No serializable constructor found -" + type.simpleName)

    type.memberProperties.forEach { prop ->

      val annotation = prop.annotations.find {
        it is Serializer.Serialize
      }

      if (annotation != null && !prop.isConst && prop is KMutableProperty<*>) {

        prop.setter.call(instance, values[prop.name])
      }
    }

    return instance
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

    return when (fieldType) {
      Boolean::class -> nullCheck(value as Boolean)
      String::class -> nullCheck(value as String)
      Int::class -> nullCheck(value as Int)
      Float::class -> nullCheck(value as Float)
      Double::class -> nullCheck(value as Double)
      Long::class -> nullCheck(value as Long)
      JSONObject::class -> nullCheck(value as JSONObject)
      JSONArray::class -> nullCheck(value as JSONArray)
      List::class -> {
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
      Set::class -> {
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
      Map::class -> {

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
    serializers: Map<KClass<*>, Serializer<Any>>
  ): Map<String, Any?> {

    val out = HashMap<String, Any?>()
    for (prop in type.memberProperties) {
      val annotation = prop.annotations.find {
        it is Serializer.Serialize
      }

      if (annotation != null) {
        annotation as Serializer.Serialize
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

            if (serializers.containsKey(fieldType)) {
              Log.e("serilizer", "" + fieldType)
              serializers[fieldType]!!.deserialize(
                fieldType,
                json.getJSONObject(jsonName),
                serializers
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
                      it.name == "add" && it.parameters.size == 2
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
                              serializers
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
                    val valueType = (prop.returnType.arguments[1].type)?.classifier as KClass<*>

                    if (keyType != String::class) {
                      throw Exception("Map key type must be string ${keyType.simpleName} - $name")
                    }

                    val put = java.util.HashMap::class.declaredFunctions.find {
                      it.name == "put"
                    }

                    jsonObject.keys().forEach {
                      put?.call(
                        map, it, when (valueType) {
                          Boolean::class -> jsonObject.getBoolean(it)
                          String::class -> jsonObject.getString(it)
                          Int::class -> jsonObject.getInt(it)
                          Float::class -> jsonObject.getDouble(it).toFloat()
                          Double::class -> jsonObject.getDouble(it)
                          Long::class -> jsonObject.getDouble(it).toLong()
                          JSONObject::class -> jsonObject.getJSONObject(it)
                          JSONArray::class -> jsonObject.getJSONArray(it)
                          else -> {
                            if (JsonUtil.isJsonObject(jsonObject, it)) {
                              val temp = deserialize(
                                valueType,
                                jsonObject.getJSONObject(it),
                                serializers
                              )
                              temp
                            } else
                              null
                          }
                        }
                      )
                    }

                  }
                  map
                }
                Set::class -> {
                  val jsonArray = json.getJSONArray(jsonName)

                  val list = HashSet<Any>()

                  if (prop.returnType.arguments[0].type != null) {
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
                              serializers
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
                  deserialize(fieldType, json.getJSONObject(jsonName), serializers)
                }

              }
          }
      }

    }
    return out
  }

}