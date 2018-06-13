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

  UNKNOWN_APP_ID,// App Id is unacceptable.
  INTERNAL_SERVER_ISSUE,// Server encountered an error processing the request.
  INVALID_PAYLOAD,// Request paylaod is invalid.
  TOKEN_UNAVAILABLE,// User token (phone, email) is already taken.
  INVALID_DATE_FORMAT,// Date format is invalid (e.g. invalid birthday in update user call).
  MALFORMED_REQUEST_BODY,// Invalid request payload on an action.
  VATOM_NOT_FOUND,// vAtom is unrecognized by the platform.
  UNKNOWN_USER_TOKEN,// User token (phone, email, id) is unrecognized by the platfrom.
  AUTHENTICATION_FAILED,// Login phone/email wrong. password
  AVATAR_UPLOAD_FAILED, // Uploading the avatar data. failed.
  USER_REFRESH_TOKEN_INVALID,// Refresh token is not on the whitelist, or the token has expired.
  AUTHENTICATION_LIMITED,// Too many login requests.
  UNABLE_TO_RETRIEVE_TOKEN,//???
  UNKNOWN_TOKEN_ID,// Token id does not map to a token.
  CANNOT_DELETE_PRIMARY_TOKEN,// Primary token cannot be deleted.
  TOKEN_ALREADY_CONFIRMED,// Attempting to verfiy an already verified token.
  INVALID_VERIFICATION_CODE,// Invalid verification code used when attempting to verify an account.
  UNKNOWN_TOKEN_TYPE,// Unrecognized token type (only `phone` and `email` are currently accepted).
  INVALID_EMAIL_ADDRESS,// Invalid email address.
  INVALID_PHONE_NUMBER,// Invalid phone number.
  USER_ACCESS_TOKEN_INVALID;

  companion object {

    private val MAP: MutableMap<Int, Error> = HashMap<Int, Error>()

    init {
      MAP[2] = UNKNOWN_APP_ID
      MAP[11] = INTERNAL_SERVER_ISSUE
      MAP[17] = UNKNOWN_APP_ID
      MAP[516] = INVALID_PAYLOAD
      MAP[517] = INVALID_PAYLOAD
      MAP[521] = TOKEN_UNAVAILABLE
      MAP[527] = INVALID_DATE_FORMAT
      MAP[1004] = MALFORMED_REQUEST_BODY
      MAP[1701] = VATOM_NOT_FOUND
      MAP[2030] = UNKNOWN_USER_TOKEN
      MAP[2032] = AUTHENTICATION_FAILED
      MAP[2037] = AVATAR_UPLOAD_FAILED
      MAP[2049] = USER_REFRESH_TOKEN_INVALID
      MAP[2051] = AUTHENTICATION_LIMITED
      MAP[2552] = UNABLE_TO_RETRIEVE_TOKEN
      MAP[2553] = UNKNOWN_TOKEN_ID
      MAP[2562] = CANNOT_DELETE_PRIMARY_TOKEN
      MAP[2566] = TOKEN_ALREADY_CONFIRMED
      MAP[2567] = INVALID_VERIFICATION_CODE
      MAP[2569] = UNKNOWN_TOKEN_TYPE
      MAP[2571] = INVALID_EMAIL_ADDRESS
      MAP[2572] = INVALID_PHONE_NUMBER

    }

    fun from(code: Int?): Error? = MAP[code]
  }
}