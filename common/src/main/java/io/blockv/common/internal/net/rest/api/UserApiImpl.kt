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
package io.blockv.common.internal.net.rest.api

import android.util.Log
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.Client
import io.blockv.common.internal.net.rest.request.*
import io.blockv.common.internal.net.rest.response.BaseResponse
import io.blockv.common.model.PublicUser
import io.blockv.common.model.Token
import io.blockv.common.model.User
import org.json.JSONArray
import org.json.JSONObject

class UserApiImpl(
  val client: Client,
  val jsonModule: JsonModule
) : UserApi {
  override fun setDefaultUserToken(tokenId: String): BaseResponse<Unit> {
    val response: JSONObject = client.put("v1/user/tokens/$tokenId/default")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      Unit
    )
  }

  override fun deleteUserToken(tokenId: String): BaseResponse<Unit> {
    val response: JSONObject = client.del("v1/user/tokens/$tokenId")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      Unit
    )
  }

  override fun getPublicUser(userId: String): BaseResponse<PublicUser> {
    val response: JSONObject = client.get("v1/users/$userId")
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(payload)
    )
  }

  override fun createUserToken(request: CreateTokenRequest): BaseResponse<Token> {
    val response: JSONObject = client.post("v1/user/tokens", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(payload)
    )
  }

  override fun createUserOauthToken(request: CreateOauthTokenRequest): BaseResponse<Token> {
    val response: JSONObject = client.post("v1/user/tokens", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(payload)
    )
  }

  override fun uploadAvatar(request: UploadAvatarRequest): BaseResponse<Unit> {
    val response: JSONObject = client.multipart(
      "v1/user/avatar",
      request.fieldName,
      request.fileName,
      request.type,
      request.payload
    )
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      Unit
    )
  }

  override fun loginGuest(request: GuestLoginRequest): BaseResponse<User> {
    val response: JSONObject = client.http("POST", "v1/user/login", request.toJson(), false)
    val payload: JSONObject = response.optJSONObject("payload")
    val user: JSONObject = payload.optJSONObject("user")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(user)
    )
  }

  override fun oauthLogin(request: OauthLoginRequest): BaseResponse<User> {
    val response: JSONObject = client.http("POST", "v1/user/login", request.toJson(), false)
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(payload)
    )
  }

  override fun register(request: CreateUserRequest): BaseResponse<User> {
    Log.e("register", request.toJson().toString())
    val response: JSONObject = client.http("POST", "v1/users", request.toJson(), false)
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(payload.optJSONObject("user"))
    )
  }

  override fun login(request: LoginRequest): BaseResponse<User> {
    Log.e("login", request.toJson().toString())
    val response: JSONObject = client.http("POST", "v1/user/login", request.toJson(), false)
    val payload: JSONObject = response.optJSONObject("payload")
    val user: JSONObject = payload.optJSONObject("user")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(user)
    )
  }

  override fun getCurrentUser(): BaseResponse<User> {
    val response: JSONObject = client.get("v1/user")
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(payload)
    )
  }

  override fun updateCurrentUser(request: UpdateUserRequest): BaseResponse<User> {
    val response: JSONObject = client.patch("v1/user", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(payload)
    )
  }

  override fun resetVerificationToken(request: ResetTokenRequest): BaseResponse<Token> {
    val response: JSONObject = client.post("v1/user/reset_token_verification", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      jsonModule.deserialize(payload)
    )
  }

  override fun resetToken(request: ResetTokenRequest): BaseResponse<Unit> {
    val response: JSONObject = client.post("v1/user/reset_token", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      Unit
    )
  }

  override fun verifyToken(request: VerifyTokenRequest): BaseResponse<Unit> {
    val response: JSONObject = client.post("v1/user/verify_token", request.toJson())
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      Unit
    )
  }

  override fun getUserTokens(): BaseResponse<List<Token>> {
    val response: JSONObject = client.get("v1/user/tokens")
    val payload: JSONArray = response.optJSONArray("payload")
    val list: ArrayList<Token> = ArrayList()
    var count = 0
    while (count < payload.length()) {
      val token: Token? = jsonModule.deserialize(payload.getJSONObject(count))
      if (token != null) {
        list.add(token)
      }
      count++
    }

    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      list
    )
  }

  override fun logout(): BaseResponse<JSONObject> {
    val response: JSONObject = client.post("v1/user/logout", null)
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optInt("error"),
      response.optString("message"),
      payload
    )
  }

}