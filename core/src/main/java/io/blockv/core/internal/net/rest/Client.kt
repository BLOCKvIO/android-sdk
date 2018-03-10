package io.blockv.core.internal.net.rest

import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/22.
 */
interface Client
{
  fun http(method: String, endpoint: String, payload: JSONObject?):JSONObject

  fun get(endpoint: String):JSONObject

  fun del(endpoint: String):JSONObject

  fun put(endpoint: String):JSONObject

  fun post(endpoint: String, payload: JSONObject?):JSONObject

  fun patch(endpoint: String, payload: JSONObject?):JSONObject

  fun multipart(endpoint: String, fieldName: String, fileName: String, type: String, payload: ByteArray, retry: Int):JSONObject

  fun multipart(endpoint: String, fieldName: String, fileName: String, type: String, payload: ByteArray):JSONObject
}