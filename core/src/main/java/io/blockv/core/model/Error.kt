package io.blockv.core.model

import java.util.*

/**
 * Created by LordCheddar on 2018/02/22.
 */
enum class Error {

  AUTHENTICATION_FAILED,
  TOKEN_EXPIRED,
  INVALID_TOKEN,
  INVALID_PAYLOAD,
  INVALID_DATE_FORMAT,
  TOKEN_UNAVAILABLE,
  INVALID_USER,
  TOKEN_ALREADY_CONFIRMED,
  INVALID_VERIFICATION_CODE,
  INVALID_PHONE_NUMBER,
  UNABLE_TO_RETRIEVE_TOKEN;

  companion object {

    private val MAP: MutableMap<Int, Error> = HashMap<Int,Error>()

    init {
      MAP.put(401, TOKEN_EXPIRED)
      MAP.put(516, INVALID_PAYLOAD)//empty string as token value
      MAP.put(521, TOKEN_UNAVAILABLE)
      MAP.put(527, INVALID_DATE_FORMAT)

      MAP.put(2030, INVALID_USER)
      MAP.put(2031, AUTHENTICATION_FAILED)
      MAP.put(2032, AUTHENTICATION_FAILED)
      MAP.put(2034, INVALID_USER)//user with token does not exist
      MAP.put(2552, UNABLE_TO_RETRIEVE_TOKEN)
      MAP.put(2563, TOKEN_ALREADY_CONFIRMED)
      MAP.put(2564, INVALID_VERIFICATION_CODE)
      MAP.put(2569, INVALID_PHONE_NUMBER)

    }

    fun from(code: Int?): Error? = MAP[code]
  }
}