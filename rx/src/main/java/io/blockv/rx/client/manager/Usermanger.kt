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

import io.blockv.core.client.manager.UserManager.*
import io.blockv.core.model.Token
import io.blockv.core.model.User
import io.reactivex.Completable
import io.reactivex.Single

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