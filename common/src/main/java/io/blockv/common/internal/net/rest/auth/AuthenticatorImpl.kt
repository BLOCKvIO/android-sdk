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
package io.blockv.common.internal.net.rest.auth

import android.util.Log
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.HttpRequest
import io.blockv.common.internal.repository.Preferences
import io.blockv.common.model.Jwt
import org.json.JSONObject
import java.util.concurrent.Semaphore
import kotlin.collections.set

class AuthenticatorImpl(val preferences: Preferences, val jsonModule: JsonModule) : Authenticator {

  @Volatile
  private var accessToken: Jwt? = null
  private val lock: Semaphore = Semaphore(1)

  override fun refreshToken(): Jwt? {
    try {

      if (lock.availablePermits() > 0) {
        lock.acquire()
        Log.e("httpclient", "refrshing token")

        val jwt: Jwt? = preferences.refreshToken
        if (jwt != null) {

          val request = HttpRequest()
          val headers: HashMap<String, String> = HashMap()
          headers["Authorization"] = jwt.type + " " + jwt.token
          headers["App-Id"] = preferences.environment!!.appId
          request.headers = headers
          request.method = "POST"
          if (preferences.environment != null) {
            request.endpoint = preferences.environment!!.rest + "v1/access_token"
          }
          val requestResponse = request.execute()
          if (requestResponse.first == 200) {
            val response = requestResponse.second

            if (response.has("payload") && (response.get("payload") is JSONObject)) {
              val pay: JSONObject = response.getJSONObject("payload")
              if (pay.has("access_token")) {
                accessToken = jsonModule.jctDeserializer.deserialize(pay.getJSONObject("access_token"))

              }
            }
          }
        }

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

  @Synchronized
  override fun getToken(): Jwt? {
    if (preferences.refreshToken == null) {
      return null
    }
    try {
      lock.acquire()
      return accessToken
    } catch (ignored: InterruptedException) {
    } finally {
      lock.release()
    }
    return accessToken
  }

  override fun setToken(token: Jwt?) {
    accessToken = token
  }
}