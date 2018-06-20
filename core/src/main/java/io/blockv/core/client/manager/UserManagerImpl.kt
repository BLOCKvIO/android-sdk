/**
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.client.manager

import android.graphics.Bitmap
import android.util.Log
import io.blockv.core.internal.net.rest.api.UserApi
import io.blockv.core.internal.net.rest.auth.Authenticator
import io.blockv.core.internal.net.rest.auth.JwtDecoder
import io.blockv.core.internal.net.rest.auth.JwtDecoderImpl
import io.blockv.core.internal.net.rest.request.*
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.model.Jwt
import io.blockv.core.model.PublicUser
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.blockv.core.util.Callable
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class UserManagerImpl(var api: UserApi,
                      var authenticator: Authenticator,
                      var preferences: Preferences,
                      var jwtDecoder: JwtDecoder) : UserManager {

  override fun addCurrentUserToken(token: String,
                                   tokenType: UserManager.TokenType,
                                   isDefault: Boolean): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {
      api.createUserToken(CreateTokenRequest(tokenType.name.toLowerCase(), token, isDefault)).payload
      return null
    }
  }

  override fun addCurrentUserOauthToken(token: String,
                                        tokenType: String,
                                        code: String,
                                        isDefault: Boolean): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {
      api.createUserOauthToken(CreateOauthTokenRequest(tokenType, token, code, isDefault)).payload
      return null
    }
  }

  override fun setCurrentUserDefaultToken(tokenId: String): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {
      api.setDefaultUserToken(tokenId).payload
      return null
    }
  }

  override fun deleteCurrentUserToken(tokenId: String): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {
      api.deleteUserToken(tokenId).payload
      return null
    }
  }

  override fun getPublicUser(userId: String): Callable<PublicUser?> = object : Callable<PublicUser?>() {
    override fun getResult(): PublicUser? = api
      .getPublicUser(userId).payload
  }


  override fun getAccessToken(): Callable<Jwt?> = object : Callable<Jwt?>() {
    override fun getResult(): Jwt? {
      return authenticator.refreshToken()
    }
  }

  override fun isLoggedIn(): Boolean {
    val token = preferences.refreshToken
    if (token != null) {
      try {
        val expired = jwtDecoder.decode(token).checkIsExpired()
        Log.i("UserManager", "token has expired $expired")
        return !expired
      } catch (exception: JwtDecoderImpl.InvalidTokenException) {
        Log.i("UserManager", exception.message)
      }
    }
    return false
  }

  override fun uploadAvatar(avatar: Bitmap): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {
      val stream = ByteArrayOutputStream()
      avatar.compress(Bitmap.CompressFormat.PNG, 100, stream)
      return api.uploadAvatar(UploadAvatarRequest("avatar", "avatar.png", "image/png", stream.toByteArray())).payload
    }
  }

  override fun loginGuest(guestId: String): Callable<User?> = object : Callable<User?>() {
    override fun getResult(): User? = api
      .loginGuest(GuestLoginRequest(
        guestId)).payload
  }

  override fun loginOauth(provider: String,
                          oauthToken: String): Callable<User?> = object : Callable<User?>() {
    override fun getResult(): User? = api
      .oauthLogin(OauthLoginRequest(
        provider,
        oauthToken)).payload
  }


  private fun login(token: String,
                    tokenType: String,
                    auth: String): Callable<User?> = object : Callable<User?>() {
    override fun getResult(): User? = api
      .login(LoginRequest(
        tokenType,
        token,
        auth)).payload
  }

  override fun login(token: String,
                     tokenType: UserManager.TokenType,
                     password: String): Callable<User?> = login(token, tokenType.name.toLowerCase(), password)

  private fun resetToken(token: String,
                         type: String): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {

      api.resetToken(ResetTokenRequest(type, token)).payload

      Log.e("reset", "reset success")
      return null
    }
  }

  override fun resetToken(token: String,
                          tokenType: UserManager.TokenType): Callable<Void?> = resetToken(token, tokenType.name.toLowerCase())

  private fun resendVerification(token: String, type: String): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {
      api.resetVerificationToken(ResetTokenRequest(type, token))
      return null
    }
  }

  override fun resendVerification(token: String,
                                  tokenType: UserManager.TokenType): Callable<Void?> = resendVerification(token, tokenType.name.toLowerCase())

  override fun register(registration: UserManager.Registration): Callable<User?> = object : Callable<User?>() {
    override fun getResult(): User? {

      val tokens = JSONArray()

      registration.tokens?.forEach {
        val data = JSONObject()
        data.put("token_type", it.type)
        data.put("token", it.value)
        if (it is UserManager.Registration.OauthToken) {
          data.put("auth_data", JSONObject().put("auth_data", it.auth))
        }
        tokens.put(data)
      }

      return api.register(CreateUserRequest(
        registration.firstName,
        registration.lastName,
        registration.birthday,
        registration.avatarUri,
        registration.password,
        registration.language,
        tokens)).payload
    }
  }

  private fun verifyUserToken(token: String,
                              type: String,
                              code: String): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {
      api.verifyToken(VerifyTokenRequest(type, token, code))
      return null
    }
  }

  override fun verifyUserToken(token: String,
                               tokenType: UserManager.TokenType,
                               code: String): Callable<Void?> = verifyUserToken(token, tokenType.name.toLowerCase(), code)

  override fun logout(): Callable<Void?> = object : Callable<Void?>() {
    override fun getResult(): Void? {
      preferences.refreshToken = null
      //remove asset providers
      api.logout()
      return null
    }
  }

  override fun getCurrentUser(): Callable<User?> = object : Callable<User?>() {
    override fun getResult(): User? {
      return api.getCurrentUser().payload
    }
  }

  override fun getCurrentUserTokens(): Callable<List<Token>> = object : Callable<List<Token>>() {
    override fun getResult(): List<Token> = api.getUserTokens().payload
  }

  override fun updateCurrentUser(update: UserManager.UserUpdate): Callable<User?> = object : Callable<User?>() {
    override fun getResult(): User? = api.updateCurrentUser(UpdateUserRequest(
      update.firstName,
      update.lastName,
      update.birthday,
      update.avatarUri,
      update.language,
      update.password
    )).payload
  }
}