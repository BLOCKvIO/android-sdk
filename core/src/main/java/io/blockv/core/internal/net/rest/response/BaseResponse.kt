package io.blockv.android.core.internal.net.rest.response

/**
 * Created by LordCheddar on 2018/02/24.
 */
class BaseResponse<T>(val status:String?,
                      val error:Int?,
                      val message:String?,
                      val payload:T)
