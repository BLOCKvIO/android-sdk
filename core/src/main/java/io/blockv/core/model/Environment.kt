package io.blockv.core.model

/**
 * Created by LordCheddar on 2017/11/10.
 */

class Environment(val rest: String,
                  val appId: String) {
  companion object {
    val DEFAULT_SERVER = "https://api.blockv.io/"
  }

}