package io.blockv.core.internal.net.rest


import android.util.Log
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.exception.BlockvException
import io.blockv.core.internal.net.rest.exception.ErrorMapper
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.model.Environment
import io.blockv.core.model.Error
import io.blockv.core.model.Jwt
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Semaphore


/**
 * Created by LordCheddar on 2018/02/22.
 */
class HttpClient(val preferences: Preferences,
                 val errorMapper: ErrorMapper,
                 val jsonModule: JsonModule) : Client {

  var readTimeout: Int? = null
  var connectTimeout: Int? = null
  var environment: Environment? = preferences.environment


  @Volatile private var accessToken: Jwt? = null
  private val lock: Semaphore = Semaphore(1)

  constructor(preferences: Preferences,
              errorMapper: ErrorMapper,
              jsonModule: JsonModule,
              readTimeout: Int?,
              connectTimeout: Int?) : this(preferences, errorMapper, jsonModule) {
    this.readTimeout = readTimeout
    this.connectTimeout = connectTimeout
  }

  internal fun http(authentication: String?,
                    method: String,
                    endpoint: String,
                    payload: JSONObject?,
                    retry: Int): JSONObject {

    if (environment == null) {
      environment = preferences.environment
      if (environment == null) {
        throw NullPointerException("Environment is null")
      }
    }
    val url: URL = URL(environment!!.rest + endpoint)
    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

    if (readTimeout != null) {
      connection.readTimeout = readTimeout as Int
    }

    if (connectTimeout != null) {
      connection.connectTimeout = connectTimeout as Int
    }
    try {
      if (authentication != null) {
        connection.setRequestProperty("Authorization", "Bearer " + authentication)
      }
      connection.setRequestProperty("X-Vatomic-App-Id", environment!!.appId)
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

      var response: JSONObject = JSONObject()

      try {

        response = JSONObject(String(buffer.toByteArray()))
        //response = response.optJSONObject("payload")
      } catch (exception: Exception) {
        Log.e("HttpClient", exception.toString())
      }
      if (responseCode == 200) {

        try {
          val pay: JSONObject = response.getJSONObject("payload")
          if (pay.has("refresh_token")) {

            preferences.refreshToken = jsonModule.jwtDeserilizer.deserialize(pay.getJSONObject("refresh_token"))

          }
          if (pay.has("access_token")) {
            accessToken = jsonModule.jwtDeserilizer.deserialize(pay.getJSONObject("access_token"))

          }

        } catch (e: Exception) {
          Log.e("httpCLient", e.toString())
        }
        return response
      } else {
        val exception: BlockvException = errorMapper.map(responseCode, response)
        Log.e("httpCLient", exception.toString())
        if (exception.error == Error.TOKEN_EXPIRED && retry == 0) {
          connection.disconnect()
          return http(refreshToken()?.token ?: "", method, endpoint, payload, 1)
        }
        throw exception

      }


    } finally {
      connection.disconnect()
    }

  }

  override fun http(method: String, endpoint: String, payload: JSONObject?): JSONObject {
    val jwt: Jwt? = getToken()

    return http(if (jwt != null) jwt.type + ":" + jwt.token else "", method, endpoint, payload, 0)

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

  private fun refreshToken(): Jwt? {
    try {

      if (lock.availablePermits() > 0) {
        lock.acquire()
        Log.e("httpclient", "refrshing token")
        val jwt: Jwt? = preferences.refreshToken;
        if (jwt != null) {
          val response: JSONObject = http(jwt.type + ":" + jwt.token, "POST", "access_token", null, 0)
          accessToken = jsonModule.jwtDeserilizer.deserialize(response.optJSONObject("access_token"))
        } else
          accessToken = null

        Log.e("httpclient", "" + accessToken)
      } else {
        lock.acquire()
      }
    } catch (e: Exception) {
      Log.e("httpclient", "" + e)
    } finally {
      lock.release()
    }
    return accessToken
  }

  @Synchronized private fun getToken(): Jwt? {
    try {
      lock.acquire()
      return accessToken
    } catch (ignored: InterruptedException) {
    } finally {
      lock.release()
    }
    return accessToken
  }
}