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

package io.blockv.common.builder

import io.blockv.common.model.UserUpdate

class UpdateUserBuilder {

  private var firstName: String? = null
  private var lastName: String? = null
  private var birthday: String? = null
  private var avatar: String? = null
  private var password: String? = null
  private var language: String? = null

  fun setFirstName(firstName: String): UpdateUserBuilder {
    this.firstName = firstName
    return this
  }

  fun setLastName(lastName: String): UpdateUserBuilder {
    this.lastName = lastName
    return this
  }

  fun setBirthday(birthday: String): UpdateUserBuilder {
    this.birthday = birthday
    return this
  }

  fun setAvatarUri(avatar: String): UpdateUserBuilder {
    this.avatar = avatar
    return this
  }

  fun setPassword(password: String): UpdateUserBuilder {
    this.password = password
    return this
  }

  fun setLanguage(language: String): UpdateUserBuilder {
    this.language = language
    return this
  }

  fun build(): UserUpdate {
    return UserUpdate(
      firstName,
      lastName,
      birthday,
      avatar,
      password,
      language
    )
  }


}