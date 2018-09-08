package io.blockv.common.model

class Registration(
  var firstName: String?,
  var lastName: String?,
  var birthday: String?,
  var avatarUri: String?,
  var password: String?,
  var language: String?,
  var tokens: List<Token>?
) {
  open class Token(val type: String, val value: String)

  class OauthToken(type: String, value: String, val auth: String) : Token(type, value)

}