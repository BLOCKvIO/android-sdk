package io.blockv.android.core.internal.net.rest


import android.util.Log
import io.blockv.core.internal.net.rest.exception.BlockvException
import io.blockv.core.internal.net.rest.exception.ErrorMapper
import io.blockv.android.core.internal.repository.Preferences
import io.blockv.core.model.Environment
import io.blockv.core.model.Error
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
                 val errorMapper: ErrorMapper) : Client {

  var readTimeout: Int? = null
  var connectTimeout: Int? = null
  var environment: Environment? = preferences.environment


  @Volatile internal var accessToken: String = ""
  internal val lock: Semaphore = Semaphore(1)

  constructor(preferences: Preferences,
              errorMapper: ErrorMapper,
              readTimeout: Int?,
              connectTimeout: Int?) : this(preferences, errorMapper) {
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
      if(authentication!=null) {
        connection.setRequestProperty("Authorization", "Bearer "+authentication)
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

      Log.e("httpclient",""+responseCode)
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
            preferences.refreshToken = pay.getString("refresh_token")
            Log.e("httpCLient",preferences.refreshToken )
          }
          if (pay.has("access_token")) {
            accessToken = pay.getString("access_token")
            Log.e("httpCLient",accessToken )
          }

        } catch (e: Exception) {
          Log.e("httpCLient",e.toString())
        }
        return response
      } else {
        val exception: BlockvException = errorMapper.map(responseCode, response)
        Log.e("httpCLient",exception.toString())
        if (exception.error == Error.TOKEN_EXPIRED && retry == 0) {
          connection.disconnect()
          return http(refreshToken(), method, endpoint, payload, 1)
        }
        throw exception

      }


    } finally {
      connection.disconnect()
    }

  }

  override fun http(method: String, endpoint: String, payload: JSONObject?): JSONObject {
    return http(getToken(), method, endpoint, payload, 0)

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

  internal fun refreshToken(): String {
    try {

      if (lock.availablePermits() > 0) {
        lock.acquire()
        Log.e("httpclient","refrshing token")
        val response: JSONObject = http(preferences.refreshToken, "POST", "access_token", null, 0)
        accessToken = response.optString("access_token")

        Log.e("httpclient",""+accessToken)
      } else {
        lock.acquire()
      }
    } catch (e: Exception) {
      Log.e("httpclient",""+e)
    } finally {
      lock.release()
    }
    return accessToken
  }

  @Synchronized internal fun getToken(): String {
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