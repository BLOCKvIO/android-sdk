package io.blockv.common.model

class OauthData(
  val user: User,
  val flow: Flow
) : Model {
  override fun toString(): String {
    return "OauthData{" +
      " user='" + user + '\'' +
      ", flow='" + flow + '\'' +
      "}"
  }
  enum class Flow(val value: String)
  {
    LOGIN("login"),
    REGISTER("register")
  }
}