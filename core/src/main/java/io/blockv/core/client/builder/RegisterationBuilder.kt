package io.blockv.core.client.builder

import io.blockv.core.client.manager.UserManager
import java.util.*

/**
 * Created by LordCheddar on 2018/01/22.
 */

class RegistrationBuilder {

  internal var firstName: String? = null
  internal var lastName: String? = null
  internal var birthday: String? = null
  internal var avatar: String? = null
  internal var password: String? = null
  internal var language: String? = null
  internal var tokens: MutableList<UserManager.Registration.Token> = ArrayList()

  fun setFirstName(firstName: String): RegistrationBuilder {
    this.firstName = firstName
    return this
  }

  fun setLastName(lastName: String): RegistrationBuilder {
    this.lastName = lastName
    return this
  }

  fun setBirthday(birthday: String): RegistrationBuilder {
    this.birthday = birthday
    return this
  }

  fun setAvatarUri(avatar: String): RegistrationBuilder {
    this.avatar = avatar
    return this
  }

  fun setPassword(password: String): RegistrationBuilder {
    this.password = password
    return this
  }

  fun addToken(token: UserManager.Registration.Token): RegistrationBuilder {
    tokens.add(token)
    return this
  }

  fun addEmail(email: String): RegistrationBuilder {
    tokens.add(UserManager.Registration.Token("email", email))
    return this
  }
  fun addPhoneNumber(phoneNumber: String): RegistrationBuilder {
    tokens.add(UserManager.Registration.Token("phone_number", phoneNumber))
    return this
  }
  fun addOauth(provider:String,token:String,auth:String): RegistrationBuilder {
    tokens.add(UserManager.Registration.OauthToken(provider,token,auth))
    return this
  }

  fun setLanguage(language: String): RegistrationBuilder {
    this.language = language
    return this
  }

  fun build(): UserManager.Registration {
    return UserManager.Registration(
      firstName,
      lastName,
      birthday,
      avatar,
      password,
      language,
      tokens)
  }


}