package io.blockv.common.internal.json

import io.blockv.common.internal.json.deserializer.Deserializer
import io.blockv.common.internal.json.deserializer.EnvironmentDeserialzier
import io.blockv.common.internal.json.deserializer.JwtDeserializer
import io.blockv.common.internal.json.deserializer.activity.ActivityMessageDeserializer
import io.blockv.common.internal.json.deserializer.activity.ActivityMessageListDeserializer
import io.blockv.common.internal.json.deserializer.activity.ActivityThreadDeserializer
import io.blockv.common.internal.json.deserializer.activity.ActivityThreadListDeserializer
import io.blockv.common.internal.json.deserializer.event.ActivityEventDeserializer
import io.blockv.common.internal.json.deserializer.event.InventoryEventDeserializer
import io.blockv.common.internal.json.deserializer.event.StateEventDeserializer
import io.blockv.common.internal.json.deserializer.event.WebsocketEventDeserializer
import io.blockv.common.internal.json.deserializer.resource.AssetProviderDeserialzier
import io.blockv.common.internal.json.deserializer.user.PublicUserDeserializer
import io.blockv.common.internal.json.deserializer.user.TokenDeserializer
import io.blockv.common.internal.json.deserializer.user.UserDeserializer
import io.blockv.common.internal.json.deserializer.vatom.*
import io.blockv.common.internal.json.serializer.Serializer
import io.blockv.common.internal.json.serializer.user.AssetProviderSerializer
import io.blockv.common.internal.json.serializer.user.EnviromentSerializer
import io.blockv.common.internal.json.serializer.user.JwtSerializer
import io.blockv.common.model.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.kotlinProperty

class JsonModule {

  val TAG: String = "JsonModule"

  val deserializers: HashMap<KClass<*>, Deserializer<*>> = HashMap()

  val serializers: HashMap<KClass<*>, Serializer<Any>> = HashMap()

  val genericSerializer: Serializer<Any> = JsonSerializer()

  init {
    registerDeserializer<ActivityMessage>(ActivityMessageDeserializer())
    registerDeserializer<ActivityThread>(ActivityThreadDeserializer())
    registerDeserializer<ActivityMessageList>(ActivityMessageListDeserializer())
    registerDeserializer<ActivityThreadList>(ActivityThreadListDeserializer())

    registerDeserializer<ActivityEvent>(ActivityEventDeserializer())
    registerDeserializer<InventoryEvent>(InventoryEventDeserializer())
    registerDeserializer<StateUpdateEvent>(StateEventDeserializer())
    registerDeserializer<WebSocketEvent<JSONObject>>(WebsocketEventDeserializer())

    registerDeserializer<AssetProvider>(AssetProviderDeserialzier())

    registerDeserializer<PublicUser>(PublicUserDeserializer())
    registerDeserializer<Token>(TokenDeserializer())
    registerDeserializer<User>(UserDeserializer())

    registerDeserializer<Action>(ActionDeserializer())
    registerDeserializer<Face>(FaceDeserializer())
    registerDeserializer<Vatom>(VatomDeserializer())
    registerDeserializer<Inventory>(InventoryDeserializer())
    registerDeserializer<DiscoverPack>(DiscoverDeserializer())
    registerDeserializer<GeoGroup>(GeoGroupDeserializer())

    registerDeserializer<Environment>(EnvironmentDeserialzier())
    registerDeserializer<Jwt>(JwtDeserializer())


    registerSerializer<AssetProvider>(AssetProviderSerializer())
    registerSerializer<Environment>(EnviromentSerializer())
    registerSerializer<Jwt>(JwtSerializer())
  }

  inline fun <reified T : Any> registerDeserializer(deserializer: Deserializer<*>) {
    val key = T::class
    deserializers[key] = deserializer
  }

  inline fun <reified T : Any> registerSerializer(serializer: Serializer<*>) {
    val key = T::class
    serializers[key] = serializer as Serializer<Any>
  }

  fun <T> deserialize(json: JSONObject, type: KClass<*>): T? {

    System.out.println("deserilizing ${type.qualifiedName}")
    val deserializer = deserializers[type]

    return deserializer?.deserialize(type, json, deserializers) as T
  }


  fun <T : Any> serialize(data: T): JSONObject? {

    val serializer = serializers[data::class]

    return if (serializer != null) {
      serializer.serialize(data, serializers)
    } else
      genericSerializer.serialize(data, serializers)

  }

  annotation class Serialize(val name: String = "", val path: String = "")
  annotation class Serializable

  open class JsonDeserializer<T> : Deserializer<T>() {

    companion object {
      val stringClass = String::class
      val intClass = Int::class
      val floatClass = Float::class
      val doubleClass = Double::class
      val longClass = Double::class
      val listClass = List::class
      val mapClass = Map::class
      val setClass = Set::class
      val jsonObjectClass = JSONObject::class
      val jsonArrayClass = JSONArray::class
    }

    override fun deserialize(
      type: KClass<*>,
      json: JSONObject,
      deserializers: Map<KClass<*>, Deserializer<*>>
    ): T? {
      val values: HashMap<String, Any?> = HashMap()
      for (it in type.java.declaredFields) {
        val fieldType = it.type

        it.kotlinProperty?.annotations?.forEach { annotation ->
          if (annotation is Serialize) {
            when {
              stringClass.isSubclassOf(fieldType.kotlin) -> values[it.name] = json.optString(annotation.name)
              intClass.isSubclassOf(fieldType.kotlin) -> values[it.name] = json.optInt(annotation.name)
              floatClass.isSubclassOf(fieldType.kotlin) -> values[it.name] = json.optDouble(annotation.name).toFloat()
              doubleClass.isSubclassOf(fieldType.kotlin) -> values[it.name] = json.optDouble(annotation.name)
              longClass.isSubclassOf(fieldType.kotlin) -> values[it.name] = json.optDouble(annotation.name).toLong()
              jsonObjectClass.isSubclassOf(fieldType.kotlin) -> values[it.name] = json.optJSONObject(annotation.name)
              jsonArrayClass.isSubclassOf(fieldType.kotlin) -> values[it.name] = json.optJSONArray(annotation.name)
              listClass.isSubclassOf(fieldType.kotlin) -> {
                val array = json.optJSONArray(annotation.name) ?: JSONArray()

                val name = it.genericType.toString().replace("java.util.List<", "").replace(">", "")

                val clss = Class.forName(name).kotlin

                if (deserializers[clss] != null) {
                  val list = deserializers[clss]!!.newList() as ArrayList<Any>

                  for (index in 0 until array.length()) {
                    val value = deserializers[clss]!!.deserialize(clss, array.getJSONObject(index), deserializers)
                    if (value != null) {
                      list.add(value)
                    }
                  }
                  values[it.name] = list
                } else
                  throw NullPointerException("No deserializer registered for $clss")
              }
              else -> {
                // values[it.name] = json.opt(annotation.name)
              }
            }
          }

        }
      }


      type.constructors.forEach {

        if (it.annotations.find { annotation ->
            annotation is Serializable
          } != null) {

          val param: HashMap<KParameter, Any?> = HashMap()
          it.parameters.forEach { parm ->
            param[parm] = values[parm.name]
          }
          return it.callBy(param) as T
        }

      }


      throw Exception("deserilization failed..")
    }

  }

  open class JsonSerializer : Serializer<Any> {

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
      for (it in clss.declaredMemberProperties) {
        val fieldType = it.returnType.classifier as KClass<*>

        it.annotations.forEach { annotation ->
          if (annotation is Serialize) {
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
      }
      return out
    }

  }
}