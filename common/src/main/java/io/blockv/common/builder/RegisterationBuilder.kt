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

import io.blockv.common.model.Registration
import java.util.*

class RegistrationBuilder {

  private var firstName: String? = null
  private var lastName: String? = null
  private var birthday: String? = null
  private var avatar: String? = null
  private var password: String? = null
  private var language: String? = null
  private var tokens: MutableList<Registration.Token> = ArrayList()

  fun setFirstName(firstName: String?): RegistrationBuilder {
    if (firstName != null) {
      this.firstName = firstName
    }
    return this
  }

  fun setLastName(lastName: String?): RegistrationBuilder {
    if (lastName != null) {
      this.lastName = lastName
    }
    return this
  }

  fun setBirthday(birthday: String?): RegistrationBuilder {
    if (birthday != null) {
      this.birthday = birthday
    }
    return this
  }

  fun setPassword(password: String?): RegistrationBuilder {
    if (password != null) {
      this.password = password
    }
    return this
  }

  fun addToken(token: Registration.Token): RegistrationBuilder {
    tokens.add(token)
    return this
  }

  fun addEmail(email: String?): RegistrationBuilder {
    if (email != null && email.isNotEmpty()) {
      tokens.add(Registration.Token("email", email))
    }
    return this
  }

  fun addPhoneNumber(phoneNumber: String?): RegistrationBuilder {
    if (phoneNumber != null && phoneNumber.isNotEmpty()) {
      tokens.add(Registration.Token("phone_number", phoneNumber))
    }
    return this
  }

  fun addOauth(provider: String?, token: String?, auth: String?): RegistrationBuilder {

    if (provider != null
      && token != null
      && auth != null
    ) {

      tokens.add(Registration.OauthToken(provider, token, auth))
    }
    return this
  }

  fun setLanguage(language: String?): RegistrationBuilder {
    if (language != null) {
      this.language = language
    }
    return this
  }

  fun build(): Registration {
    return Registration(
      firstName,
      lastName,
      birthday,
      avatar,
      password,
      language,
      tokens
    )
  }


}