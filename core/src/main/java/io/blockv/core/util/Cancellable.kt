package io.blockv.core.util

/**
 * Created by LordCheddar on 2018/02/21.
 */
interface Cancellable {

  fun isComplete(): Boolean

  fun isCanceled(): Boolean

  fun cancel()

}