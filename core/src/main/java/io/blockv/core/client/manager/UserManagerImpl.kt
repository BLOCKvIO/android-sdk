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
package io.blockv.core.client.manager

import android.graphics.Bitmap
import android.util.Log
import io.blockv.common.internal.net.rest.api.UserApi
import io.blockv.common.internal.net.rest.auth.Authenticator
import io.blockv.common.internal.net.rest.auth.JwtDecoder
import io.blockv.common.internal.net.rest.auth.JwtDecoderImpl
import io.blockv.common.internal.net.rest.request.*
import io.blockv.common.internal.repository.Preferences
import io.blockv.common.model.*
import io.blockv.common.util.Optional
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class UserManagerImpl(
  val api: UserApi,
  val authenticator: Authenticator,
  val preferences: Preferences,
  val jwtDecoder: JwtDecoder
) : UserManager {

  override fun addCurrentUserToken(
    token: String,
    tokenType: UserManager.TokenType,
    isDefault: Boolean
  ): Single<Token> = Single.fromCallable {
    api.createUserToken(CreateTokenRequest(tokenType.name.toLowerCase(), token, isDefault)).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun addCurrentUserOauthToken(
    token: String,
    tokenType: String,
    code: String, isDefault: Boolean
  ): Single<Token> = Single.fromCallable {
    api.createUserOauthToken(CreateOauthTokenRequest(tokenType, token, code, isDefault)).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun setCurrentUserDefaultToken(tokenId: String): Completable = Completable.fromCallable {
    api.setDefaultUserToken(tokenId)
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun deleteCurrentUserToken(tokenId: String): Completable = Completable.fromCallable {
    api.deleteUserToken(tokenId)
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getPublicUser(userId: String): Single<PublicUser> = Single.fromCallable {
    api.getPublicUser(userId).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun register(registration: Registration): Single<User> = Single.fromCallable {
    val tokens = JSONArray()
    registration.tokens?.forEach {
      val data = JSONObject()
      data.put("token_type", it.type)
      data.put("token", it.value)
      if (it is Registration.OauthToken) {
        data.put("auth_data", JSONObject().put("auth_data", it.auth))
      }
      tokens.put(data)
    }
    api.register(
      CreateUserRequest(
        registration.firstName,
        registration.lastName,
        registration.birthday,
        registration.avatarUri,
        registration.password,
        registration.language,
        tokens
      )
    ).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun login(
    token: String,
    tokenType: UserManager.TokenType,
    password: String
  ): Single<User> = Single.fromCallable {
    api.login(
      LoginRequest(
        tokenType.name.toLowerCase(),
        token,
        password
      )
    ).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun loginOauth(
    provider: String,
    oauthToken: String
  ): Single<User> = Single.fromCallable {
    api.oauthLogin(
      OauthLoginRequest(
        provider,
        oauthToken
      )
    ).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun loginGuest(guestId: String): Single<User> = Single.fromCallable {
    api.loginGuest(
      GuestLoginRequest(
        guestId
      )
    ).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun verifyUserToken(
    token: String,
    tokenType: UserManager.TokenType,
    code: String
  ): Completable = Completable.fromCallable {
    api.verifyToken(VerifyTokenRequest(tokenType.name.toLowerCase(), token, code))
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun resetToken(
    token: String,
    tokenType: UserManager.TokenType
  ): Completable = Completable.fromCallable {
    api.resetToken(ResetTokenRequest(tokenType.name.toLowerCase(), token)).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun resendVerification(
    token: String,
    tokenType: UserManager.TokenType
  ): Completable = Completable.fromCallable {
    api.resetVerificationToken(ResetTokenRequest(tokenType.name.toLowerCase(), token))
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getCurrentUser(): Single<User> = Single.fromCallable {
    api.getCurrentUser().payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun updateCurrentUser(update: UserUpdate): Single<User> = Single.fromCallable {
    api.updateCurrentUser(
      UpdateUserRequest(
        update.firstName,
        update.lastName,
        update.birthday,
        update.avatarUri,
        update.language,
        update.password
      )
    ).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getCurrentUserTokens(): Single<List<Token>> = Single.fromCallable {
    api.getUserTokens().payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun logout(): Completable = Completable.fromCallable {
    preferences.refreshToken = null
    api.logout()
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun uploadAvatar(avatar: Bitmap): Completable = Completable.fromCallable {
    val stream = ByteArrayOutputStream()
    avatar.compress(Bitmap.CompressFormat.PNG, 100, stream)
    api.uploadAvatar(UploadAvatarRequest("avatar", "avatar.png", "image/png", stream.toByteArray())).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

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

  override fun getAccessToken(): Single<Optional<Jwt>> = Single.fromCallable {
    Optional(authenticator.refreshToken())
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}