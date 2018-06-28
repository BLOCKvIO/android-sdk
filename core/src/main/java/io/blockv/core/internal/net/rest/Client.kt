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
package io.blockv.core.internal.net.rest

import org.json.JSONObject

interface Client {

  fun http(method: String, endpoint: String, payload: JSONObject?): JSONObject

  fun get(endpoint: String): JSONObject

  fun del(endpoint: String): JSONObject

  fun put(endpoint: String): JSONObject

  fun post(endpoint: String, payload: JSONObject?): JSONObject

  fun patch(endpoint: String, payload: JSONObject?): JSONObject

  fun multipart(endpoint: String, fieldName: String, fileName: String, type: String, payload: ByteArray, retry: Int): JSONObject

  fun multipart(endpoint: String, fieldName: String, fileName: String, type: String, payload: ByteArray): JSONObject


}