package io.blockv.rx.client.manager


import io.blockv.core.internal.net.rest.request.CreateUserRequest
import io.blockv.core.internal.net.rest.request.ResetTokenRequest
import io.blockv.core.internal.net.rest.request.UpdateUserRequest
import io.blockv.core.internal.net.rest.request.VerifyTokenRequest
import io.blockv.core.client.manager.UserManager.UserUpdate
import io.blockv.core.internal.net.rest.api.UserApi
import io.blockv.core.internal.net.rest.request.LoginRequest
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/25.
 */
class UserManagerImpl(var api: UserApi) : UserManager {

  private fun login(tokenType: String, token: String, auth: String): Single<User?> = Single.fromCallable {
    api.login(LoginRequest(
      tokenType,
      token,
      auth)).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun loginEmail(email: String, password: String): Single<User?> = login("email", email, password)

  override fun loginPhoneNumber(phoneNumber: String, password: String): Single<User?> =
    login("phone_number", phoneNumber, password)

  private fun sendOtp(type: String, token: String): Completable =
    Completable.fromCallable { api.resetToken(ResetTokenRequest(type, token)) }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())

  override fun sendOtpPhoneNumber(phone: String): Completable = sendOtp("phone_number", phone)

  override fun sendOtpEmail(email: String): Completable = sendOtp("email", email)

  private fun sendVerification(type: String, token: String): Completable = Completable.fromCallable {
    api.resetToken(ResetTokenRequest(type, token))
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun sendVerificationPhoneNumber(phoneNumber: String): Completable = sendVerification("phone_number", phoneNumber)

  override fun sendVerificationEmail(email: String): Completable = sendVerification("email", email)

  override fun register(registration: io.blockv.core.client.manager.UserManager.Registration): Single<User?> =
    Single.fromCallable {
      val tokens = JSONArray()
      registration.tokens?.forEach {
        val data = JSONObject()
        data.put("token_type", it.type)
        data.put("token", it.value)
        if (it is io.blockv.core.client.manager.UserManager.Registration.OauthToken) {
          data.put("auth_data", JSONObject().put("auth_data", it.auth))
        }
      }
      api.register(CreateUserRequest(
        registration.firstName,
        registration.lastName,
        registration.birthday,
        registration.avatarUri,
        registration.password,
        registration.language,
        tokens)).payload
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())


  private fun verify(type: String, token: String, code: String): Completable = Completable.fromCallable {
    api.verifyToken(VerifyTokenRequest(type, token, code))
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun verifyPhoneNumber(phone: String, code: String): Completable = verify("phone_number", phone, code)

  override fun verifyEmail(email: String, code: String): Completable = verify("email", email, code)

  override fun logout(): Completable = Completable.fromCallable {
    api.logout()
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getCurrentUser(): Single<User?> = Single.fromCallable {
    api.getCurrentUser().payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getCurrentUserTokens(): Single<List<Token>> = Single.fromCallable {
    api.getUserTokens().payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun updateCurrentUser(update: UserUpdate): Single<User?> = Single.fromCallable {
    api.updateCurrentUser(UpdateUserRequest(
      update.firstName,
      update.lastName,
      update.birthday,
      update.avatarUri,
      update.language,
      update.password
    )).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}