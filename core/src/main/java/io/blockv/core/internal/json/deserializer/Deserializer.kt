package io.blockv.core.internal.json.deserializer

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/24.
 */
interface Deserializer<out T> {

  fun deserialize(data: JSONObject): T?
}