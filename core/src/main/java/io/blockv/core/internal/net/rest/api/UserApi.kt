package io.blockv.core.internal.net.rest.api

import io.blockv.core.internal.net.rest.request.*
import io.blockv.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.Token
import io.blockv.core.model.User
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/21.
 */
interface UserApi {

  fun register(request: CreateUserRequest): BaseResponse<User?>

  fun login(request: LoginRequest): BaseResponse<User?>

  fun loginGuest(request: GuestLoginRequest): BaseResponse<User?>

  fun oauthLogin(request: OauthLoginRequest): BaseResponse<User?>

  fun getCurrentUser(): BaseResponse<User?>

  fun updateCurrentUser(request: UpdateUserRequest): BaseResponse<User?>

  fun resetVerificationToken(request: ResetTokenRequest): BaseResponse<Token?>

  fun resetToken(request: ResetTokenRequest): BaseResponse<Void?>

  fun verifyToken(request: VerifyTokenRequest): BaseResponse<Void?>

  fun getUserTokens(): BaseResponse<List<Token>>

  fun logout(): BaseResponse<JSONObject>

  fun uploadAvatar(request: UploadAvatarRequest): BaseResponse<Void?>

}