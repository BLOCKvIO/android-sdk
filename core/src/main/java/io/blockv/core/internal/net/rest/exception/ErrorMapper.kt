package io.blockv.core.internal.net.rest.exception

import io.blockv.core.internal.net.rest.exception.BlockvException
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/22.
 */
interface ErrorMapper {
  fun map(httpCode:Int,payload: JSONObject?): BlockvException
}