package io.blockv.rx.client.manager

import io.blockv.core.client.manager.UserManager.*
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.reactivex.Completable
import io.reactivex.Single


/**
 * Created by LordCheddar on 2018/02/22.
 */
interface UserManager {

  fun loginEmail(email: String, password: String): Single<User?>

  fun loginPhoneNumber(phoneNumber: String, password: String): Single<User?>

  fun sendOtpPhoneNumber(phone: String): Completable

  fun sendOtpEmail(email: String): Completable

  fun sendVerificationPhoneNumber(phoneNumber: String): Completable

  fun sendVerificationEmail(email: String): Completable

  fun register(registration: Registration): Single<User?>

  fun verifyPhoneNumber(phone: String, code: String): Completable

  fun verifyEmail(email: String, code: String): Completable

  fun logout(): Completable

  fun getCurrentUser(): Single<User?>

  fun getCurrentUserTokens(): Single<List<Token>>

  fun updateCurrentUser(update: UserUpdate): Single<User?>

}