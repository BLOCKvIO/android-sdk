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

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import io.blockv.common.internal.net.rest.api.UserApi
import io.blockv.common.internal.net.rest.auth.Authenticator
import io.blockv.common.internal.net.rest.auth.JwtDecoder
import io.blockv.common.internal.net.rest.auth.JwtDecoderImpl
import io.blockv.common.internal.net.rest.request.CreateOauthTokenRequest
import io.blockv.common.internal.net.rest.request.CreateTokenRequest
import io.blockv.common.internal.net.rest.request.CreateUserRequest
import io.blockv.common.internal.net.rest.request.GuestLoginRequest
import io.blockv.common.internal.net.rest.request.LoginRequest
import io.blockv.common.internal.net.rest.request.OauthLoginRequest
import io.blockv.common.internal.net.rest.request.ResetTokenRequest
import io.blockv.common.internal.net.rest.request.TokenRequest
import io.blockv.common.internal.net.rest.request.UpdateUserRequest
import io.blockv.common.internal.net.rest.request.UploadAvatarRequest
import io.blockv.common.internal.net.rest.request.VerifyTokenRequest
import io.blockv.common.internal.repository.Preferences
import io.blockv.common.model.Jwt
import io.blockv.common.model.PublicUser
import io.blockv.common.model.Registration
import io.blockv.common.model.Token
import io.blockv.common.model.User
import io.blockv.common.model.UserUpdate
import io.blockv.common.util.Optional
import io.blockv.core.internal.datapool.Inventory
import io.blockv.core.internal.oauth.BlockvOauthException
import io.blockv.core.internal.oauth.OauthActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class UserManagerImpl(
  val api: UserApi,
  val authenticator: Authenticator,
  val preferences: Preferences,
  val jwtDecoder: JwtDecoder,
  val inventory: Inventory
) : UserManager {

  override var onLogoutListener: UserManager.LogoutListener? = null

  init {
    authenticator.onUnAuthorizedListener = {
      onLogoutListener?.onLogout()
    }
  }

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

  override fun setCurrentUserDefaultToken(tokenId: String): Single<JSONObject> = Single.fromCallable {
    api.setDefaultUserToken(tokenId).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun deleteCurrentUserToken(tokenId: String): Single<JSONObject> = Single.fromCallable {
    api.deleteUserToken(tokenId).payload
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

  override fun loginOauth(context: Context, scope: String): Single<User> {
    return Single.create<User> { emitter ->
      val environment = preferences.environment!!
      val disposable = CompositeDisposable()
      emitter.setDisposable(disposable)
      OauthActivity.start(context, environment.appId, environment.redirectUri, scope, object : OauthActivity.Handler {
        override fun onSuccess(code: String): Single<User> {
          return Single.fromCallable {
            api.getAccessTokens(TokenRequest(environment.appId, code, environment.redirectUri))
          }.subscribeOn(Schedulers.io())
            .map {
              preferences.refreshToken = Jwt(it.getString("refresh_token"), "bearer")
              authenticator.setToken(Jwt(it.getString("access_token"), "bearer"))
              api.refreshAssetProviders()
              api.getCurrentUser().payload
            }
            .doOnSuccess {
              emitter.onSuccess(it)
            }
            .doOnError { emitter.onError(it) }
        }

        override fun onError(exception: BlockvOauthException) {
          emitter.onError(exception)
        }

      })
    }.subscribeOn(AndroidSchedulers.mainThread())
  }

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
  ): Single<JSONObject> = Single.fromCallable {
    api.verifyToken(VerifyTokenRequest(tokenType.name.toLowerCase(), token, code)).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun resetToken(
    token: String,
    tokenType: UserManager.TokenType
  ): Single<JSONObject> = Single.fromCallable {
    api.resetToken(ResetTokenRequest(tokenType.name.toLowerCase(), token)).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun resendVerification(
    token: String,
    tokenType: UserManager.TokenType
  ): Single<JSONObject> = Single.fromCallable {
    api.resetVerificationToken(ResetTokenRequest(tokenType.name.toLowerCase(), token)).payload
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

  override fun logout(): Single<JSONObject> =
    inventory.reset()
      .observeOn(AndroidSchedulers.mainThread())
      .map {
        preferences.refreshToken = null
        onLogoutListener?.onLogout()
      }
      .observeOn(Schedulers.io())
      .map {
        api.logout().payload
      }
      .observeOn(AndroidSchedulers.mainThread())

  override fun uploadAvatar(avatar: Bitmap): Single<Unit> = Single.fromCallable {
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