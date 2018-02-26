package io.blockv.core.util

/**
 * Created by LordCheddar on 2018/02/21.
 */
interface Disposable {

  fun isDisposed(): Boolean

  fun dispose()

}