package io.blockv.core.client.manager

import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.blockv.core.util.Observable

/**
 *  This interface contains the available Blockv user functions
 */
interface UserManager {


  /**
   * Registers a user on the Blockv platform.
   *
   * @param registration contains properties of the user. Only the properties to be registered should be set.
   * @return new Observable<User> instance
   */
  fun register(registration: Registration): Observable<User?>


  /**
   * Logs a user into the Blockv platform. Accepts a user token (phone or email).
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @param password the user's password.
   * @return new Observable<User> instance
   */
  fun login(token: String,tokenType:TokenType, password: String): Observable<User?>

  /**
   * Logs a user into the Blockv platform. Accepts an OAuth token.
   *
   * @param provider the OAuth provider, e.g. Facebook.
   * @param oauthToken the OAuth token issued by the OAuth provider.
   * @return new Observable<User> instance
   */
  fun loginOauth(provider: String, oauthToken: String): Observable<User?>

  /**
   * Verifies ownership of a token by submitting the verification code to the Blockv platform.
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @param code the verification code send to the user's token (phone or email).
   * @return new Observable<User> instance
   */
  fun verifyToken(token: String,tokenType:TokenType, code: String): Observable<Void?>

  /**
   * Sends a One-Time-Pin (OTP) to the user's token (phone or email).
   *
   * This OTP may be used in place of a password to login.
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @return new Observable<Void> instance
   */
  fun sendLoginOtp(token: String,tokenType:TokenType): Observable<Void?>

  /**
   * Sends a verification code to the user's token (phone or email).
   *
   * This verification code should be used to verifiy the user's ownership of the token (phone or email).
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @return new Observable<Void> instance
   */
  fun sendVerificationCode(token: String,tokenType:TokenType): Observable<Void?>

  /**
   * Fetches the current user's profile information from the Blockv platform.
   *
   * @return new Observable<User> instance
   */
  fun getCurrentUser(): Observable<User?>

  /**
   * Updates the current user's profile on the Blockv platform.
   *
   * @param update holds the properties of the user, e.g. their first name. Only the properties to be updated should be set.
   * @return new Observable<User> instance
   */
  fun updateCurrentUser(update: UserUpdate): Observable<User?>

  fun getCurrentUserTokens(): Observable<List<Token>>

  /**
   * Log out the current user.
   *
   * The current user will not longer be authorized to perform user scoped requests on the Blockv platfrom.
   *
   * @return new Observable<Void> instance
   */
  fun logout(): Observable<Void?>


  enum class TokenType
  {
    EMAIL,
    PHONE_NUMBER
  }

  class Registration(var firstName: String?,
                     var lastName: String?,
                     var birthday: String?,
                     var avatarUri: String?,
                     var password: String?,
                     var language: String?,
                     var tokens: List<Token>?)
  {
    open class Token(val type:String,val value:String)

    class OauthToken(type:String,value:String,val auth:String):Token(type,value)
  }

  class UserUpdate(var firstName: String?,
                   var lastName: String?,
                   var birthday: String?,
                   var avatarUri: String?,
                   var password: String?,
                   var language: String?)


}