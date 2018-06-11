package io.blockv.core.internal.net.rest.auth

import io.blockv.core.model.DecodedJwt
import io.blockv.core.model.Jwt

interface JwtDecoder {

fun decode(jwt:Jwt):DecodedJwt

}