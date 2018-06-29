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

import android.util.Log
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpRequest {

  var method: String = "GET"
  var endpoint: String? = null
  var payload: JSONObject? = null
  var readTimeout: Int? = null
  var connectTimeout: Int? = null
  var headers: Map<String, String>? = null


  fun execute(): Pair<Int, JSONObject> {

    if (endpoint == null) {
      throw NullPointerException("Endpoint is null")
    }

    val url: URL = URL(endpoint)
    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

    if (readTimeout != null) {
      connection.readTimeout = readTimeout as Int
    }

    if (connectTimeout != null) {
      connection.connectTimeout = connectTimeout as Int
    }
    try {
      headers!!.forEach {
        connection.setRequestProperty(it.key, it.value)
      }
      connection.setRequestProperty("Content-Type", "application/json")
      connection.requestMethod = method
      connection.useCaches = false
      connection.doInput = true

      if (payload != null) {
        connection.doOutput = true
        val out: DataOutputStream = DataOutputStream(connection.outputStream)
        out.write(payload.toString().toByteArray())
        out.flush()
        out.close()
      }

      val responseCode: Int = connection.responseCode

      Log.e("httpclient", "" + responseCode)
      val input: DataInputStream
      if (responseCode == 200) {
        input = DataInputStream(connection.inputStream)
      } else {
        input = DataInputStream(connection.errorStream)
      }
      val buffer = ByteArrayOutputStream()

      var read: Int
      val data = ByteArray(16384)

      do {
        read = input.read(data, 0, data.size)
        if (read != -1) {
          buffer.write(data, 0, read)
        }
      } while (read != -1)

      buffer.flush()

      var response = JSONObject()
      try {
        response = JSONObject(String(buffer.toByteArray()))
      } catch (exception: Exception) {
        Log.e("HttpClient", exception.toString())
      }
      return Pair(responseCode, response)

    } finally {
      connection.disconnect()
    }

  }

}