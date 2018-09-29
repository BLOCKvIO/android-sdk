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

import io.blockv.common.internal.net.rest.request.*
import io.blockv.common.internal.net.rest.response.BaseResponse
import io.blockv.common.model.PublicUser
import io.blockv.common.model.Token
import io.blockv.common.model.User
import org.json.JSONObject

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

  fun createUserToken(request: CreateTokenRequest): BaseResponse<Token?>

  fun createUserOauthToken(request: CreateOauthTokenRequest): BaseResponse<Token?>

  fun setDefaultUserToken(tokenId: String): BaseResponse<Void?>

  fun deleteUserToken(tokenId: String): BaseResponse<Void?>

  fun getPublicUser(userId: String): BaseResponse<PublicUser?>

  fun logout(): BaseResponse<JSONObject>

  fun uploadAvatar(request: UploadAvatarRequest): BaseResponse<Void?>

}