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
package io.blockv.core.internal.net.rest.exception

import io.blockv.core.model.Error
import org.json.JSONObject

class DefaultErrorMapper : ErrorMapper {

  override fun map(httpCode: Int, payload: JSONObject?): BlockvException {

    if (payload == null) {
      return BlockvException(httpCode, "An networking error has occurred", null, null)
    }

    try {
      val error: Int = payload.optInt("error", 0)
      val message: String = payload.optString("message", "An networking error has occurred")
      return BlockvException(httpCode, message, error, Error.from(if (error > 0) error else httpCode))
    } catch (exception: Exception) {
      return BlockvException(httpCode, exception.message, null, null)
    }

  }
}