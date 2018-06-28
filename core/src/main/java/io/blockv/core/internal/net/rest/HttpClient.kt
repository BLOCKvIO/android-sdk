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
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.auth.Authenticator
import io.blockv.core.internal.net.rest.exception.BlockvException
import io.blockv.core.internal.net.rest.exception.ErrorMapper
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.model.resource.AssetProvider
import io.blockv.core.model.Environment
import io.blockv.core.model.Error
import io.blockv.core.model.Jwt
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*
import kotlin.collections.HashMap

class HttpClient(val preferences: Preferences,
                 val errorMapper: ErrorMapper,
                 val jsonModule: JsonModule,
                 val authenticator: Authenticator) : Client {

  var readTimeout: Int? = null
  var connectTimeout: Int? = null
  var environment: Environment? = preferences.environment


  constructor(preferences: Preferences,
              errorMapper: ErrorMapper,
              jsonModule: JsonModule,
              authenticator: Authenticator,
              readTimeout: Int?,
              connectTimeout: Int?) : this(preferences, errorMapper, jsonModule,authenticator) {
    this.readTimeout = readTimeout
    this.connectTimeout = connectTimeout
  }

  internal fun http(method: String,
                    endpoint: String,
                    payload: JSONObject?,
                    retry: Int): JSONObject {

    if (environment == null) {
      environment = preferences.environment
      if (environment == null) {
        throw NullPointerException("Environment is null")
      }
    }

    val request = HttpRequest()
    request.endpoint = environment!!.rest + endpoint
    request.readTimeout = readTimeout
    request.connectTimeout = connectTimeout
    request.method = method
    val headers = HashMap<String, String>()

    val token = authenticator.getToken()
    if (token != null) {
      headers["Authorization"] = token.type + " " + token.token
    }
    headers["App-Id"] = environment!!.appId
    headers["Content-Type"] = "application/json"
    request.headers = headers
    request.payload = payload

    val requestResponse = request.execute()
    if (requestResponse.first == 200) {
      val response: JSONObject = requestResponse.second
      try {
        if (response.has("payload") && (response.get("payload") is JSONObject)) {
          val pay: JSONObject = response.getJSONObject("payload")
          if (pay.has("refresh_token")) {
            preferences.refreshToken = jsonModule.jctDeserializer.deserialize(pay.getJSONObject("refresh_token"))
          }

          if (pay.has("access_token")) {
            authenticator.setToken(jsonModule.jctDeserializer.deserialize(pay.getJSONObject("access_token")))

          }
          if (pay.has("asset_provider")) {
            val assetProviders = pay.getJSONArray("asset_provider")
            val assetProviderArray = ArrayList<AssetProvider>()
            (0 until assetProviders.length()).forEach {
              val assetProvider = jsonModule.assetProviderDeserializer.deserialize(assetProviders.getJSONObject(it))
              if (assetProvider != null) {
                assetProviderArray.add(assetProvider)
              }
            }
            preferences.assetProviders = assetProviderArray
          }
        }
      } catch (e: Exception) {
        Log.e("httpCLient", e.toString())
      }
      return response
    } else {
      val exception: BlockvException = errorMapper.map(requestResponse.first, requestResponse.second)
      Log.e("httpCLient", exception.toString())
      if (exception.error == Error.USER_ACCESS_TOKEN_INVALID && retry == 0) {
        authenticator.refreshToken()
        return http( method, endpoint, payload, 1)
      }
      throw exception

    }

  }

  override fun http(method: String, endpoint: String, payload: JSONObject?): JSONObject {
    return http( method, endpoint, payload, 0)

  }

  override fun get(endpoint: String): JSONObject {
    return http("GET", endpoint, null)
  }

  override fun del(endpoint: String): JSONObject {
    return http("DEL", endpoint, null)
  }

  override fun put(endpoint: String): JSONObject {
    return http("PUT", endpoint, null)
  }

  override fun post(endpoint: String, payload: JSONObject?): JSONObject {
    return http("POST", endpoint, payload)
  }

  override fun patch(endpoint: String, payload: JSONObject?): JSONObject {
    return http("PATCH", endpoint, payload)
  }

  override fun multipart(endpoint: String, fieldName: String, fileName: String, type: String, payload: ByteArray): JSONObject {
    return multipart(endpoint, fieldName, fileName, type, payload, 0)
  }

  override fun multipart(endpoint: String, fieldName: String, fileName: String, type: String, payload: ByteArray, retry: Int): JSONObject {
    if (environment == null) {
      environment = preferences.environment
      if (environment == null) {
        throw NullPointerException("Environment is null")
      }
    }
    val url = URL(environment!!.rest + endpoint)
    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

    if (readTimeout != null) {
      connection.readTimeout = readTimeout as Int
    }

    if (connectTimeout != null) {
      connection.connectTimeout = connectTimeout as Int
    }

    val jwt: Jwt? = authenticator.getToken()

    try {
      if (jwt != null) {
        connection.setRequestProperty("Authorization", jwt.type + " " + jwt.token)
      }
      val boundary = "1234567890"

      connection.setRequestProperty("X-Vatomic-App-Id", environment!!.appId)
      connection.useCaches = false
      connection.doInput = true
      connection.doOutput = true
      connection.requestMethod = "POST"
      connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary)
      connection.setRequestProperty(fieldName, fieldName)

      val output = connection.outputStream

      val writer = PrintWriter(OutputStreamWriter(output, "UTF-8"), true)
      writer
        .append("--" + boundary)
        .append("\r\n")
        .append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fieldName + "\"")
        .append("\r\n")
        .append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName))
        .append("\r\n")
        .append("Content-Transfer-Encoding: binary")
        .append("\r\n")
        .append("\r\n")
        .flush()

      output
        .write(payload)
      output.flush()

      writer
        .append("\r\n")
        .flush()
      writer
        .append("\r\n")
        .flush()
      writer.append("--" + boundary + "--")
        .append("\r\n")
      writer.close()

      val responseCode: Int = connection.responseCode

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
      if (responseCode == 200) {

        try {
          val pay: JSONObject = response.getJSONObject("payload")
          if (pay.has("refresh_token")) {
            preferences.refreshToken = jsonModule.jctDeserializer.deserialize(pay.getJSONObject("refresh_token"))

          }
          if (pay.has("access_token")) {
            authenticator.setToken(jsonModule.jctDeserializer.deserialize(pay.getJSONObject("access_token")))
          }

        } catch (e: Exception) {
          Log.e("httpCLient", e.toString())
        }
        return response
      } else {
        val exception: BlockvException = errorMapper.map(responseCode, response)
        Log.e("httpCLient", exception.toString())
        if (exception.error == Error.USER_ACCESS_TOKEN_INVALID && retry == 0) {
          connection.disconnect()
          return multipart(endpoint, fieldName, fileName, type, payload, 1)
        }
        throw exception

      }

    } finally {
      connection.disconnect()
    }

  }

}