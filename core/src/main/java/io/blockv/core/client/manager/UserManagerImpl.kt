package io.blockv.core.client.manager

import io.blockv.android.core.internal.net.rest.request.CreateUserRequest
import io.blockv.android.core.internal.net.rest.request.ResetTokenRequest
import io.blockv.android.core.internal.net.rest.request.UpdateUserRequest
import io.blockv.android.core.internal.net.rest.request.VerifyTokenRequest
import io.blockv.core.internal.net.rest.api.UserApi
import io.blockv.core.internal.net.rest.request.LoginRequest
import io.blockv.core.internal.net.rest.request.OauthLoginRequest
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.blockv.core.util.Observable
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/22.
 */
class UserManagerImpl(var api: UserApi) : UserManager {

  override fun loginOauth(provider: String, token: String): Observable<User?> = object : Observable<User?>() {
    override fun getResult(): User? = api
      .oauthLogin(OauthLoginRequest(
        provider,
        token)).payload
  }


  private fun login(tokenType: String, token: String, auth: String): Observable<User?> = object : Observable<User?>() {
    override fun getResult(): User? = api
      .login(LoginRequest(
        tokenType,
        token,
        auth)).payload
  }

  override fun loginEmail(email: String, password: String): Observable<User?> = login("email", email, password)

  override fun loginPhoneNumber(phoneNumber: String, password: String): Observable<User?> = login("phone_number", phoneNumber, password)

  private fun sendOtp(type: String, token: String): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {
      api.resetToken(ResetTokenRequest(type, token))
      return null
    }
  }

  override fun sendOtpPhoneNumber(phone: String): Observable<Void?> = sendOtp("phone_number", phone)

  override fun sendOtpEmail(email: String): Observable<Void?> = sendOtp("email", email)

  private fun sendVerification(type: String, token: String): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {
      api.resetVerificationToken(ResetTokenRequest(type, token))
      return null
    }
  }

  override fun sendVerificationPhoneNumber(phoneNumber: String): Observable<Void?> = sendVerification("phone_number", phoneNumber)

  override fun sendVerificationEmail(email: String): Observable<Void?> = sendVerification("email", email)

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

  private fun verify(type: String, token: String, code: String): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {
      api.verifyToken(VerifyTokenRequest(type, token, code))
      return null
    }
  }

  override fun verifyPhoneNumber(phone: String, code: String): Observable<Void?> = verify("phone_number", phone, code)

  override fun verifyEmail(email: String, code: String): Observable<Void?> = verify("email", email, code)

  override fun logout(): Observable<Void?> = object : Observable<Void?>() {
    override fun getResult(): Void? {
      api.logout()
      return null
    }
  }

  override fun getCurrentUser(): Observable<User?> = object : Observable<User?>() {
    override fun getResult(): User? = api.getCurrentUser().payload
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