package io.blockv.core.util

/**
 * Created by LordCheddar on 2018/03/06.
 */
class CompositeDisposable() : ArrayList<Disposable>() {

  fun dispose() {
    forEach {
      if (!it.isDisposed()) {
        it.dispose()
      }
    }
    clear()
  }
}