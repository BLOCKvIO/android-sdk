package io.blockv.core.model

import java.util.HashMap

/**
 * Created by LordCheddar on 2018/02/22.
 */
enum class Error
{
  AUTHENTICATION_FAILED,
  TOKEN_EXPIRED;

    companion object {

      internal val MAP: MutableMap<Int, Error> = HashMap()

      init {
        MAP.put(401, TOKEN_EXPIRED)
        MAP.put(2031, AUTHENTICATION_FAILED)
      }

      fun from(code: Int?): Error? {
        return MAP[code]
      }
    }
}