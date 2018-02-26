package io.blockv.core.internal.net.rest.exception

import io.blockv.core.model.Error
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/22.
 */
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