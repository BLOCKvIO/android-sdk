package io.blockv.core.internal.oauth

class BlockvOauthException(val error: Error) : Exception(error.message) {

  enum class Error(val message: String) {
    OAUTH_UNAVAILABLE("Error loading Blockv OAuth"),
    STATE_CHANGED("OAuth response has been tampered with"),
    ACCESS_DENIED("The user declined the permission"),
    USER_CANCEL("The OAuth activity was closed prior to finishing."),
    UNKNOWN("An error has occurred");

    fun exception(): BlockvOauthException {
      return BlockvOauthException(this)
    }
  }

}