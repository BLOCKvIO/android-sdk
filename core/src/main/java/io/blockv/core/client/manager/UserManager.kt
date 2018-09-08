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
import io.blockv.common.model.Jwt
import io.blockv.common.model.PublicUser
import io.blockv.common.model.Token
import io.blockv.common.model.User
import io.blockv.common.util.Callable

/**
 *  This interface contains the available BLOCKv user functions.
 */
interface UserManager {


  /**
   * Registers a user on the BLOCKv platform.
   *
   * @param registration contains properties of the user. Only the properties to be registered should be set.
   * @return new Callable<User> instance.
   * @see Registration
   */
  fun register(registration: Registration): Callable<User?>


  /**
   * Logs a user into the BLOCKv platform. Accepts a user token (phone or email).
   *
   * @param token the user's phone(E.164) or email.
   * @param tokenType the type of the token (phone or email).
   * @param password the user's password.
   * @return new Callable<User> instance.
   */
  fun login(token: String, tokenType: TokenType, password: String): Callable<User?>

  /**
   * Logs a user into the BLOCKv platform. Accepts an OAuth token.
   *
   * @param provider the OAuth provider, e.g. Facebook.
   * @param oauthToken the OAuth token issued by the OAuth provider.
   * @return new Callable<User> instance.
   */
  fun loginOauth(provider: String, oauthToken: String): Callable<User?>

  /**
   * Logs a user into the BLOCKv platform. Accepts a guest id.
   *
   * @param guestId the user's guest id.
   * @return new Callable<User> instance.
   */
  fun loginGuest(guestId: String): Callable<User?>

  /**
   * Verifies ownership of a token by submitting the verification code to the BLOCKv platform.
   *
   * @param token the user's phone(E.164) or email.
   * @param tokenType the type of the token (phone or email).
   * @param code the verification code send to the user's token (phone or email).
   * @return new Callable<User> instance.
   */
  fun verifyUserToken(token: String, tokenType: TokenType, code: String): Callable<Void?>

  /**
   * Sends a One-Time-Pin (OTP) to the user's token (phone or email).
   *
   * This OTP may be used in place of a password to login.
   *
   * @param token the user's phone(E.164) or email.
   * @param tokenType the type of the token (phone or email).
   * @return new Callable<Void> instance.
   */
  fun resetToken(token: String, tokenType: TokenType): Callable<Void?>

  /**
   * Sends a verification code to the user's token (phone or email).
   *
   * This verification code should be used to verify the user's ownership of the token (phone or email).
   *
   * @param token the user's phone(E.164) or email.
   * @param tokenType the type of the token (phone or email).
   * @return new Callable<Void> instance.
   */
  fun resendVerification(token: String, tokenType: TokenType): Callable<Void?>

  /**
   * Fetches the current user's profile information from the BLOCKv platform.
   *
   * @return new Callable<User> instance.
   */
  fun getCurrentUser(): Callable<User?>

  /**
   * Updates the current user's profile on the BLOCKv platform.
   *
   * @param update holds the properties of the user, e.g. their first name. Only the properties to be updated should be set.
   * @return new Callable<User> instance.
   * @see UserUpdate
   */
  fun updateCurrentUser(update: UserUpdate): Callable<User?>

  /**
   * Fetches a list of the current user's tokens.
   *
   * @return new Callable<List<Token>>.
   * @see Token
   */
  fun getCurrentUserTokens(): Callable<List<Token>>

  /**
   * Adds a user token to the current user.
   *
   * @param token the user token to be linked to the current user.
   * @param tokenType the type of the token (phone or email).
   * @param isDefault determines whether the token is the primary token on this account.
   * @return new Callable<Void>.
   */
  fun addCurrentUserToken(token: String, tokenType: TokenType, isDefault: Boolean): Callable<Token?>

  /**
   * Adds a OAuth user token to the current user.
   *
   * @param token is the users id from the oAuth provider.
   * @param tokenType is the OAuth provider (e.g facebook).
   * @param code is the auth OAuth token from the provider.
   * @param isDefault determines whether the token is the primary token on this account.
   * @return new Callable<Void>.
   */
  fun addCurrentUserOauthToken(token: String, tokenType: String, code: String, isDefault: Boolean): Callable<Token?>

  /**
   * Updates the specified token to be the current user's default token on the BLOCKv Platform.
   *
   * Backend description:
   * Boolean to indicate if this token is the primary token. The primary token is used when no other
   * token is explicitly selected, for example to send messages. This will automatically set the
   * is_primary flag of an existing token to false , because only one token can be the primary token.
   *
   * @param tokenId is the unique identifier of the token to be deleted.
   * @return new Callable<Void>.
   */
  fun setCurrentUserDefaultToken(tokenId: String): Callable<Void?>

  /**
   * Removes the token from the current user's token list on the BLOCKv Platform.
   *
   * @param tokenId is the unique identifier of the token.
   * @return new Callable<Void>
   */
  fun deleteCurrentUserToken(tokenId: String): Callable<Void?>

  /**
   * Fetches the publicly available attributes of any user given their user id.
   *
   * Since users are given control over which attributes they make public, you should make
   * provision for receiving all, some, or none of their public attributes.
   *
   * @param userId is the unique identifier of the user.
   * @return new Callable<PublicUser>.
   * @see PublicUser
   */
  fun getPublicUser(userId: String): Callable<PublicUser?>

  /**
   * Log out the current user.
   *
   * The current user will not longer be authorized to perform user scoped requests on the BLOCKv platform.
   *
   * @return new Callable<Void> instance.
   */
  fun logout(): Callable<Void?>

  /**
   * Upload Bitmap to server to be used as the user's avatar.
   *
   * @return new Callable<Void> instance.
   */
  fun uploadAvatar(avatar: Bitmap): Callable<Void?>

  /**
   * Determines whether a user is logged in.
   *
   * @return Boolean `true` if logged in. `false` otherwise.
   */
  fun isLoggedIn(): Boolean

  /**
   * Retrieves and refreshes the SDKs access token.
   *
   * This function should only be called if you have a well defined reason for obtaining an access token.
   *
   * @return new Callable<Jwt>.
   * @see Jwt
   */
  fun getAccessToken(): Callable<Jwt?>

  enum class TokenType {
    EMAIL,
    PHONE_NUMBER
  }

  class Registration(
    var firstName: String?,
    var lastName: String?,
    var birthday: String?,
    var avatarUri: String?,
    var password: String?,
    var language: String?,
    var tokens: List<Token>?
  ) {
    open class Token(val type: String, val value: String)

    class OauthToken(type: String, value: String, val auth: String) : Token(type, value)

  }

  class UserUpdate(
    var firstName: String?,
    var lastName: String?,
    var birthday: String?,
    var avatarUri: String?,
    var password: String?,
    var language: String?
  )


}