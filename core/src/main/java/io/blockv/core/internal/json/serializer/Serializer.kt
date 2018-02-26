package io.blockv.core.internal.json.serializer

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/24.
 */
interface Serializer<in T> {

  fun serialize(data: T): JSONObject
}