package io.blockv.core.internal.net.rest.request

import io.blockv.core.model.Token
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by LordCheddar on 2018/01/22.
 */
/*
 "first_name" : "Sean",
    "last_name" : "van Duffelen",
    "password": "test",
    "birthday": "16.10.1991",
    "avatar_uri": "",
    "user_tokens": [{
      "token_type": "email",
      "token": "lordcheddar+reg1@gmail.com"
    }]
 */
class CreateUserRequest {

  var firstName: String? = null
  var lastName: String? = null
  var birthday: String? = null
  var avatarUri: String? = null
  var password: String? = null
  var language: String? = null
  var tokens: JSONArray? = null

  constructor(firstName: String?,
              lastName: String?,
              birthday: String?,
              avatarUri: String?,
              password: String?,
              language: String?,
              tokens: JSONArray?) {
    this.firstName = firstName
    this.lastName = lastName
    this.birthday = birthday
    this.avatarUri = avatarUri
    this.password = password
    this.language = language
    this.tokens = tokens
  }


  fun toJson(): JSONObject {
    val json: JSONObject = JSONObject()
    if (firstName != null) json.put("first_name", firstName)
    if (lastName != null) json.put("last_name", lastName)
    if (birthday != null) json.put("birthday", birthday)
    if (avatarUri != null) json.put("avatar_uri", avatarUri)
    if (password != null) json.put("password", password)
    if (language != null) json.put("language", language)
    if (tokens != null) json.put("tokens", tokens)


    return json
  }

}
