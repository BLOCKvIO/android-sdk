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
import io.blockv.core.model.Jwt
import io.blockv.core.model.PublicUser
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.blockv.core.util.Callable

/**
 *  This interface contains the available Blockv user functions
 */
interface UserManager {


  /**
   * Registers a user on the Blockv platform.
   *
   * @param registration contains properties of the user. Only the properties to be registered should be set.
   * @return new Callable<User> instance
   */
  fun register(registration: Registration): Callable<User?>


  /**
   * Logs a user into the Blockv platform. Accepts a user token (phone or email).
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @param password the user's password.
   * @return new Callable<User> instance
   */
  fun login(token: String, tokenType: TokenType, password: String): Callable<User?>

  /**
   * Logs a user into the Blockv platform. Accepts an OAuth token.
   *
   * @param provider the OAuth provider, e.g. Facebook.
   * @param oauthToken the OAuth token issued by the OAuth provider.
   * @return new Callable<User> instance
   */
  fun loginOauth(provider: String, oauthToken: String): Callable<User?>

  /**
   * Logs a user into the Blockv platform. Accepts a guest id
   *
   * @param guestId the user's guest id.
   * @return new Callable<User> instance
   */
  fun loginGuest(guestId: String): Callable<User?>

  /**
   * Verifies ownership of a token by submitting the verification code to the Blockv platform.
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @param code the verification code send to the user's token (phone or email).
   * @return new Callable<User> instance
   */
  fun verifyUserToken(token: String, tokenType: TokenType, code: String): Callable<Void?>

  /**
   * Sends a One-Time-Pin (OTP) to the user's token (phone or email).
   *
   * This OTP may be used in place of a password to login.
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @return new Callable<Void> instance
   */
  fun resetToken(token: String, tokenType: TokenType): Callable<Void?>

  /**
   * Sends a verification code to the user's token (phone or email).
   *
   * This verification code should be used to verifiy the user's ownership of the token (phone or email).
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @return new Callable<Void> instance
   */
  fun resendVerification(token: String, tokenType: TokenType): Callable<Void?>

  /**
   * Fetches the current user's profile information from the Blockv platform.
   *
   * @return new Callable<User> instance
   */
  fun getCurrentUser(): Callable<User?>

  /**
   * Updates the current user's profile on the Blockv platform.
   *
   * @param update holds the properties of the user, e.g. their first name. Only the properties to be updated should be set.
   * @return new Callable<User> instance
   */
  fun updateCurrentUser(update: UserUpdate): Callable<User?>

  fun getCurrentUserTokens(): Callable<List<Token>>

  fun addUserToken(token: String, tokenType: TokenType, isDefault: Boolean): Callable<Void?>

  fun addUserOauthToken(token: String, tokenType: String, code: String, isDefault: Boolean): Callable<Void?>

  fun setDefaultUserToken(tokenId: String): Callable<Void?>

  fun deleteUserToken(tokenId: String): Callable<Void?>

  fun getPublicUser(userId: String): Callable<PublicUser?>

  /**
   * Log out the current user.
   *
   * The current user will not longer be authorized to perform user scoped requests on the Blockv platfrom.
   *
   * @return new Callable<Void> instance
   */
  fun logout(): Callable<Void?>

  /**
   * Upload Bitmap to server to be used as the user's avatar
   *
   * @return new Callable<Void> instance
   */
  fun uploadAvatar(avatar: Bitmap): Callable<Void?>

  fun isLoggedIn(): Boolean

  fun getAccessToken(): Callable<Jwt?>

  enum class TokenType {
    EMAIL,
    PHONE_NUMBER
  }

  class Registration(var firstName: String?,
                     var lastName: String?,
                     var birthday: String?,
                     var avatarUri: String?,
                     var password: String?,
                     var language: String?,
                     var tokens: List<Token>?) {
    open class Token(val type: String, val value: String)

    class OauthToken(type: String, value: String, val auth: String) : Token(type, value)

  }

  class UserUpdate(var firstName: String?,
                   var lastName: String?,
                   var birthday: String?,
                   var avatarUri: String?,
                   var password: String?,
                   var language: String?)


}