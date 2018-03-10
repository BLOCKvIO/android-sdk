package io.blockv.core.internal.net.rest.api

import android.util.Log
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.Client
import io.blockv.core.internal.net.rest.request.*
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.Token
import io.blockv.core.model.User
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/24.
 */
class UserApiImpl(val client: Client,
                  val jsonModule: JsonModule) : UserApi {
  override fun uploadAvatar(request: UploadAvatarRequest): BaseResponse<Void?> {
    val response: JSONObject = client.multipart(
      "v1/user/avatar",
      request.fieldName,
      request.fileName,
      request.type,
      request.payload)
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      null)
  }

  override fun loginGuest(request: GuestLoginRequest): BaseResponse<User?> {
    val response: JSONObject = client.post("v1/user/login", request.toJson())

    val payload: JSONObject = response.optJSONObject("payload")
    val user: JSONObject = payload.optJSONObject("user")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(user))
  }

  override fun oauthLogin(request: OauthLoginRequest): BaseResponse<User?> {
    val response: JSONObject = client.post("v1/user/login", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(payload))
  }

  override fun register(request: CreateUserRequest): BaseResponse<User?> {
    Log.e("register", request.toJson().toString())
    val response: JSONObject = client.post("v1/users", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(payload.optJSONObject("user")))
  }

  override fun login(request: LoginRequest): BaseResponse<User?> {
    Log.e("login", request.toJson().toString())

    val response: JSONObject = client.post("v1/user/login", request.toJson())

    val payload: JSONObject = response.optJSONObject("payload")
    val user: JSONObject = payload.optJSONObject("user")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(user))

  }

  override fun getCurrentUser(): BaseResponse<User?> {
    val response: JSONObject = client.get("v1/user")
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(payload))

  }

  override fun updateCurrentUser(request: UpdateUserRequest): BaseResponse<User?> {
    val response: JSONObject = client.patch("v1/user", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.userDeserilizer.deserialize(payload))
  }

  override fun resetVerificationToken(request: ResetTokenRequest): BaseResponse<Token?> {

    val response: JSONObject = client.post("v1/user/reset_token_verification", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      jsonModule.tokenDeserilizer.deserialize(payload))
  }

  override fun resetToken(request: ResetTokenRequest): BaseResponse<Void?> {


    val response: JSONObject = client.post("v1/user/reset_token", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      null)
  }

  override fun verifyToken(request: VerifyTokenRequest): BaseResponse<Void?> {
    val response: JSONObject = client.post("v1/user/verify_token", request.toJson())
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      null)
  }

  override fun getUserTokens(): BaseResponse<List<Token>> {
    val response: JSONObject = client.get("v1/user/tokens")
    val payload: JSONArray = response.optJSONArray("payload")
    val list: ArrayList<Token> = ArrayList()

    var count = 0
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
    val response: JSONObject = client.post("v1/user/logout", null)
    val payload: JSONObject = response.optJSONObject("payload")
    return BaseResponse(
      response.optString("status"),
      response.optInt("error"),
      response.optString("message"),
      payload)
  }

}