package io.blockv.core.client.manager

import android.graphics.Bitmap
import android.util.Log
import io.blockv.core.internal.net.rest.api.UserApi
import io.blockv.core.internal.net.rest.request.*
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.blockv.core.util.Observable
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream


/**
 * Created by LordCheddar on 2018/02/22.
 */
class UserManagerImpl(var api: UserApi,
                      var preferences: Preferences,
                      val resourceManager: ResourceManager) : UserManager {

  override fun isLoggedIn(): Boolean {
    val token = preferences.refreshToken
    return token!=null && !token.hasExpired()
  }

  override fun uploadAvatar(avatar: Bitmap): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {
      val stream = ByteArrayOutputStream()
      avatar.compress(Bitmap.CompressFormat.PNG, 100, stream)
      return api.uploadAvatar(UploadAvatarRequest("avatar","avatar.png","image/png",stream.toByteArray())).payload
    }
  }

  override fun loginGuest(guestId: String): Observable<User?> = object : Observable<User?>() {
    override fun getResult(): User? = api
      .loginGuest(GuestLoginRequest(
        guestId)).payload
  }

  override fun loginOauth(provider: String, token: String): Observable<User?> = object : Observable<User?>() {
    override fun getResult(): User? = api
      .oauthLogin(OauthLoginRequest(
        provider,
        token)).payload
  }


  private fun login(token: String, tokenType: String, auth: String): Observable<User?> = object : Observable<User?>() {
    override fun getResult(): User? = api
      .login(LoginRequest(
        tokenType,
        token,
        auth)).payload
  }

  override fun login(token: String, tokenType: UserManager.TokenType, password: String): Observable<User?> = login(token, tokenType.name.toLowerCase(), password)

  private fun resetToken(token: String, type: String): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {

      api.resetToken(ResetTokenRequest(type, token)).payload

      Log.e("reset", "reset succes")
      return null
    }
  }

  override fun resetToken(token: String, tokenType: UserManager.TokenType): Observable<Void?> = resetToken(token, tokenType.name.toLowerCase())

  private fun resendVerification(token: String, type: String): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {
      api.resetVerificationToken(ResetTokenRequest(type, token))
      return null
    }
  }

  override fun resendVerification(token: String, tokenType: UserManager.TokenType): Observable<Void?> = resendVerification(token, tokenType.name.toLowerCase())

  override fun register(registration: UserManager.Registration): Observable<User?> = object : Observable<User?>() {
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

  private fun verifyUserToken(token: String, type: String, code: String): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {
      api.verifyToken(VerifyTokenRequest(type, token, code))
      return null
    }
  }

  override fun verifyUserToken(token: String, tokenType: UserManager.TokenType, code: String): Observable<Void?> = verifyUserToken(token, tokenType.name.toLowerCase(), code)

  override fun logout(): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {
      preferences.refreshToken = null
      api.logout()
      return null
    }
  }

  override fun getCurrentUser(): Observable<User?> = object : Observable<User?>() {
    override fun getResult(): User? {
      val user: User? = api.getCurrentUser().payload
      if (user?.avatarUri != null) {
        user.avatarUri = resourceManager.encodeUrl(user.avatarUri)
      }

      return user
    }
  }

  override fun getCurrentUserTokens(): Observable<List<Token>> = object : Observable<List<Token>>() {
    override fun getResult(): List<Token> = api.getUserTokens().payload
  }

  override fun updateCurrentUser(update: UserManager.UserUpdate): Observable<User?> = object : Observable<User?>() {
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