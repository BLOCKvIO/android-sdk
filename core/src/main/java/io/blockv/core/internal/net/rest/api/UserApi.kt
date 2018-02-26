package io.blockv.core.internal.net.rest.api

import io.blockv.android.core.internal.net.rest.request.*
import io.blockv.android.core.internal.net.rest.response.BaseResponse
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.blockv.core.internal.net.rest.request.LoginRequest
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/21.
 */
interface UserApi {

  fun register(request: RegisterRequest): BaseResponse<User?>

  fun login(request: LoginRequest): BaseResponse<User?>

  fun getCurrentUser(): BaseResponse<User?>

  fun updateCurrentUser(request: UpdateUserRequest): BaseResponse<User?>

  fun resetToken(request: ResetTokenRequest): BaseResponse<Token?>

  fun verifyToken(request: VerifyTokenRequest): BaseResponse<Token?>

  fun getUserTokens(): BaseResponse<List<Token>>

  fun logout(): BaseResponse<JSONObject>

}