package io.blockv.android.core.internal.net.rest.request

import org.json.JSONObject

class UpdateUserRequest {

  var firstName: String? = null
  var lastName: String? = null
  var birthday: String? = null
  var avatarUri: String? = null
  var password: String? = null
  var language: String? = null

  constructor(firstName: String?,
              lastName: String?,
              birthday: String?,
              avatarUri: String?,
              language: String?,
              password: String?) {
    this.firstName = firstName
    this.lastName = lastName
    this.birthday = birthday
    this.avatarUri = avatarUri
    this.language = language
    this.password = password
  }

  fun toJson(): JSONObject {
    val json: JSONObject = JSONObject()
    if (firstName != null) json.put("first_name", firstName)
    if (lastName != null) json.put("last_name", lastName)
    if (birthday != null) json.put("birthday", birthday)
    if (avatarUri != null) json.put("avatar_uri", avatarUri)
    if (password != null) json.put("password", password)
    if (language != null) json.put("language", language)

    return json
  }

}
