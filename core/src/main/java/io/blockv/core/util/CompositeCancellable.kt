package io.blockv.core.util

/**
 * Created by LordCheddar on 2018/03/06.
 */
class CompositeCancellable() : ArrayList<Cancellable>() {

  fun cancel() {
    forEach {
      if (!it.isCanceled() && !it.isComplete()) {
        it.cancel()
      }
    }
    clear()
  }
}