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

  enum class Flow(val value: String) {

    LOGIN("login"),
    REGISTER("register"),
    OTHER("other");

    companion object {
      fun from(flow: String): Flow {
        return when (flow) {
          "login" -> LOGIN
          "register" -> REGISTER
          else -> OTHER
        }
      }
    }
  }
}