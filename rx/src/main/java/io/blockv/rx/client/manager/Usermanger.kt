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
package io.blockv.rx.client.manager

import android.graphics.Bitmap
import io.blockv.core.client.manager.UserManager.*
import io.blockv.core.model.PublicUser
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.reactivex.Completable
import io.reactivex.Single

/**
 *  This interface contains the available Blockv user functions
 */
interface UserManager {


  /**
   * Registers a user on the Blockv platform.
   *
   * @param registration contains properties of the user. Only the properties to be registered should be set.
   * @return new Single<User> instance
   */
  fun register(registration: Registration): Single<User>


  /**
   * Logs a user into the Blockv platform. Accepts a user token (phone or email).
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @param password the user's password.
   * @return new Single<User> instance
   */
  fun login(token: String, tokenType: TokenType, password: String): Single<User>

  /**
   * Logs a user into the Blockv platform. Accepts an OAuth token.
   *
   * @param provider the OAuth provider, e.g. Facebook.
   * @param oauthToken the OAuth token issued by the OAuth provider.
   * @return new Single<User> instance
   */
  fun loginOauth(provider: String, oauthToken: String): Single<User>

  /**
   * Logs a user into the Blockv platform. Accepts a guest id
   *
   * @param guestId the user's guest id.
   * @return new Single<User> instance
   */
  fun loginGuest(guestId: String): Single<User>

  /**
   * Verifies ownership of a token by submitting the verification code to the Blockv platform.
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @param code the verification code send to the user's token (phone or email).
   * @return new Completable instance
   */
  fun verifyUserToken(token: String, tokenType: TokenType, code: String): Completable

  /**
   * Sends a One-Time-Pin (OTP) to the user's token (phone or email).
   *
   * This OTP may be used in place of a password to login.
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @return new Completable instance
   */
  fun resetToken(token: String, tokenType: TokenType): Completable

  /**
   * Sends a verification code to the user's token (phone or email).
   *
   * This verification code should be used to verifiy the user's ownership of the token (phone or email).
   *
   * @param token the user's phone(E.164) or email
   * @param tokenType the type of the token (phone or email)
   * @return new Completable instance
   */
  fun resendVerification(token: String, tokenType: TokenType): Completable

  /**
   * Fetches the current user's profile information from the Blockv platform.
   *
   * @return new Single<User> instance
   */
  fun getCurrentUser(): Single<User>

  /**
   * Updates the current user's profile on the Blockv platform.
   *
   * @param update holds the properties of the user, e.g. their first name. Only the properties to be updated should be set.
   * @return new Single<User> instance
   */
  fun updateCurrentUser(update: UserUpdate): Single<User>

  fun getCurrentUserTokens(): Single<List<Token>>

  fun addUserToken(token: String, tokenType: TokenType, isDefault: Boolean): Completable

  fun addUserOauthToken(token: String, tokenType: String, code: String, isDefault: Boolean): Completable

  fun setDefaultUserToken(tokenId: String): Completable

  fun deleteUserToken(tokenId: String): Completable

  fun getPublicUser(userId: String): Single<PublicUser>

  /**
   * Log out the current user.
   *
   * The current user will not longer be authorized to perform user scoped requests on the Blockv platfrom.
   *
   * @return new Completable instance
   */
  fun logout(): Completable

  /**
   * Upload Bitmap to server to be used as the user's avatar
   *
   * @return new Completable instance
   */
  fun uploadAvatar(avatar: Bitmap): Completable

  fun isLoggedIn(): Boolean

  companion object {
    val NULL_USER = User()
    val NULL_PUBLIC_USER = PublicUser()
  }
}