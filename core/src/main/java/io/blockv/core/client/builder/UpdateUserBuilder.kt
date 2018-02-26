package io.blockv.core.client.builder

import io.blockv.core.client.manager.UserManager

/**
 * Created by LordCheddar on 2018/01/22.
 */

class UpdateUserBuilder {

  internal var firstName: String? = null
  internal var lastName: String? = null
  internal var birthday: String? = null
  internal var avatar: String? = null
  internal var password: String? = null
  internal var language: String? = null

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

  fun build(): UserManager.UserUpdate {
    return UserManager.UserUpdate(
      firstName,
      lastName,
      birthday,
      avatar,
      password,
      language)
  }


}