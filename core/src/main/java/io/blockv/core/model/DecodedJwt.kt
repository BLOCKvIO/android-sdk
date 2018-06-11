package io.blockv.core.model

import java.util.*

class DecodedJwt(val jwt: Jwt, val userId: String, val expiration: Date) {
  fun checkIsExpired(): Boolean {
    return Date().after(expiration)
  }
}