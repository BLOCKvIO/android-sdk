package io.blockv.face.client

import org.json.JSONObject
import kotlin.reflect.KClass

interface JsonSerializer {

  fun <T : Any> deserialize(kclass: KClass<T>, json: JSONObject): T?

  fun <T : Any> serialize(data: T): JSONObject?
}