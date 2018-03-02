package io.blockv.core.client.builder

import io.blockv.core.client.manager.UserManager

/**
 * Created by LordCheddar on 2018/01/22.
 */

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