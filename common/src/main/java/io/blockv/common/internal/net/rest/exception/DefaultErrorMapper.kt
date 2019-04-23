/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.common.internal.net.rest.exception

import io.blockv.common.model.Error
import org.json.JSONObject

class DefaultErrorMapper : ErrorMapper {

  override fun map(httpCode: Int, payload: JSONObject?): BlockvException {

    if (payload == null) {
      return BlockvException(null, httpCode, "An networking error has occurred", null, null)
    }
    return try {
      val errorCode: Int = payload.optInt("error", 0)
      val message: String = payload.optString("message", "An networking error has occurred")
      val requestId: String? = payload.optString("request_id", null)
      val error: Error?
      error = if ((httpCode == 401 && errorCode == 0)
        && payload.has("exp") && payload.getString("exp").equals("token expired", true)
        || (payload.has("message") && payload.getString("message").equals("Unauthorized", true))
      ) {
        Error.USER_ACCESS_TOKEN_INVALID
      } else
        Error.from(errorCode)
      BlockvException(requestId, httpCode, message, errorCode, error)
    } catch (exception: Exception) {
      BlockvException(null, httpCode, exception.message, null, null)
    }

  }
}