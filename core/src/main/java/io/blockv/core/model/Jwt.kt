package io.blockv.core.model

import java.util.*

/**
 * Created by LordCheddar on 2018/03/05.
 */
class Jwt(val token: String, val type: String, val expiresIn: Int, val expires: Int) {

  fun hasExpired(): Boolean = (Date().time / 1000).toInt() > expires
}

