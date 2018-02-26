package io.blockv.core.model

class User {

  var id: String? = null
  var whenCreated: String? = null
  var whenModified: String? = null
  var firstName: String? = null
  var lastName: String? = null
  var avatarUri: String? = null
  var birthday: String? = null
  var language: String? = null

  constructor(id: String?,
              whenCreated: String?,
              whenModified: String?,
              firstName: String?,
              lastName: String?,
              avatarUri: String?,
              birthday: String?,
              language: String?) {
    this.id = id
    this.whenCreated = whenCreated
    this.whenModified = whenModified
    this.firstName = firstName
    this.lastName = lastName
    this.avatarUri = avatarUri
    this.birthday = birthday
    this.language = language
  }


  override fun toString(): String {
    return "User{" +
      "id='" + id + '\'' +
      ", whenCreated='" + whenCreated + '\'' +
      ", whenModified='" + whenModified + '\'' +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", avatarUrl='" + avatarUri + '\'' +
      ", birthday='" + birthday + '\'' +
      ", language='" + language + '\'' +
      '}'
  }

}
