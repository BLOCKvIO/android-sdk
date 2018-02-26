package io.blockv.core.client.manager

import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.blockv.core.util.Observable

/**
 * Created by LordCheddar on 2018/02/22.
 */
interface UserManager {

  fun loginEmail(email: String, password: String): Observable<User?>

  fun loginPhoneNumber(phoneNumber: String, password: String): Observable<User?>

  fun sendOtpPhoneNumber(phone: String): Observable<Void?>

  fun sendOtpEmail(email: String): Observable<Void?>

  fun sendVerificationPhoneNumber(phoneNumber: String): Observable<Void?>

  fun sendVerificationEmail(email: String): Observable<Void?>

  fun register(registration: Registration): Observable<User?>

  fun verifyPhoneNumber(phone: String, code: String): Observable<Void?>

  fun verifyEmail(email: String, code: String): Observable<Void?>

  fun logout(): Observable<Void?>

  fun getCurrentUser(): Observable<User?>

  fun getCurrentUserTokens(): Observable<List<Token>>

  fun updateCurrentUser(update: UserUpdate): Observable<User?>


  class Registration(var firstName: String?,
                     var lastName: String?,
                     var birthday: String?,
                     var avatarUri: String?,
                     var password: String?,
                     var language: String?,
                     var tokens: List<Token>?)

  class UserUpdate(var firstName: String?,
                   var lastName: String?,
                   var birthday: String?,
                   var avatarUri: String?,
                   var password: String?,
                   var language: String?)
}