package io.blockv.core.internal.net.rest.exception

import io.blockv.core.model.Error
import java.io.IOException

/**
 * Created by LordCheddar on 2018/02/22.
 */
class BlockvException(var httpCode:Int,
                      message:String?,
                      var blockvCode:Int?,
                      var error: Error?): IOException(message)

{

  override fun toString():String
  {
    return "httpCode = $httpCode\nmessage = $message\nbockvCode = $blockvCode\nblockvError = $error"
  }
}


