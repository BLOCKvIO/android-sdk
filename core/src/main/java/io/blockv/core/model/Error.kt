/**
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.model

import java.util.*

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
  UNABLE_TO_RETRIEVE_TOKEN,
  MALFORMED_REQUEST_BODY,
  INVALID_DATA_VALIDATION,
  VATOM_NOT_FOUND,
  AVATAR_UPLOAD_FAILED;

  companion object {

    private val MAP: MutableMap<Int, Error> = HashMap<Int,Error>()

    init {
      MAP.put(516, INVALID_PAYLOAD)//empty string as token value
      MAP.put(521, TOKEN_UNAVAILABLE)
      MAP.put(527, INVALID_DATE_FORMAT)
      MAP.put(1004,MALFORMED_REQUEST_BODY)
      MAP.put(1041,INVALID_DATA_VALIDATION)
      MAP.put(1701,VATOM_NOT_FOUND)
      MAP.put(2030, INVALID_USER)
      MAP.put(2031, AUTHENTICATION_FAILED)
      MAP.put(2032, AUTHENTICATION_FAILED)
      MAP.put(2034, INVALID_USER)//user with token does not exist
      MAP.put(2037, AVATAR_UPLOAD_FAILED)
      MAP.put(2552, UNABLE_TO_RETRIEVE_TOKEN)
      MAP.put(2563, TOKEN_ALREADY_CONFIRMED)
      MAP.put(2564, INVALID_VERIFICATION_CODE)
      MAP.put(2569, INVALID_PHONE_NUMBER)

    }

    fun from(code: Int?): Error? = MAP[code]
  }
}