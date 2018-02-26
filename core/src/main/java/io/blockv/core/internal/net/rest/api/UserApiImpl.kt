package io.blockv.core.internal.net.rest.api

import io.blockv.android.core.internal.net.rest.Client
import io.blockv.core.internal.json.JsonModule
import io.blockv.android.core.internal.net.rest.request.*
import io.blockv.android.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.blockv.core.internal.net.rest.request.LoginRequest
import io.blockv.core.internal.net.rest.request.OauthLoginRequest
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/24.
 */
class UserApiImpl(val client: Client,
                  val jsonModule: JsonModule) : UserApi {

  override fun oauthLogin(request: OauthLoginRequest): BaseResponse<User?> {
    val response: JSONObject = client.post("users", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(payload))
  }

  override fun register(request: CreateUserRequest): BaseResponse<User?> {
    val response: JSONObject = client.post("users", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(payload))
  }

  override fun login(request: LoginRequest): BaseResponse<User?> {
    val response: JSONObject = client.post("users/login", request.toJson())

    val payload: JSONObject = response.optJSONObject("payload")
    val user: JSONObject = payload.optJSONObject("user")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(user))

  }

  override fun getCurrentUser(): BaseResponse<User?> {
    val response: JSONObject = client.get("user")
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(payload))

  }

  override fun updateCurrentUser(request: UpdateUserRequest): BaseResponse<User?> {
    val response: JSONObject = client.patch("user", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(payload))
  }

  override fun resetVerificationToken(request: ResetTokenRequest): BaseResponse<Token?> {

    val response: JSONObject = client.post("users/reset_token_verification", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.tokenDeserilizer.deserialize(payload))
  }

  override fun resetToken(request: ResetTokenRequest): BaseResponse<Token?> {

    val response: JSONObject = client.post("users/reset_token", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.tokenDeserilizer.deserialize(payload))
  }

  override fun verifyToken(request: VerifyTokenRequest): BaseResponse<Token?> {
    val response: JSONObject = client.post("users/verify_token", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.tokenDeserilizer.deserialize(payload))
  }

  override fun getUserTokens(): BaseResponse<List<Token>> {
    val response: JSONObject = client.get("user/tokens")
    val payload: JSONArray = response.optJSONArray("payload")
    val list: ArrayList<Token> = ArrayList()

    var count: Int = 0
    while (count < payload.length()) {
      val token: Token? = jsonModule.tokenDeserilizer.deserialize(payload.getJSONObject(count))
      if (token != null) {
        list.add(token)
      }
      count++
    }

    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      list)
  }

  override fun logout(): BaseResponse<JSONObject> {
    val response: JSONObject = client.post("user/logout", null)
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      payload)
  }

}