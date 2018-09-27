package io.blockv.face.client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.common.util.Cancellable
import io.blockv.common.util.CompositeCancellable


abstract class FaceView(
  var vatom: Vatom,
  var face: Face,
  val bridge: FaceBridge
) {
  private var cancellable: CompositeCancellable

  @get:Synchronized
  @set:Synchronized
  internal var isLoaded: Boolean = false

  init {
    cancellable = CompositeCancellable()
  }

  abstract fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View

  abstract fun onLoad(handler: LoadHandler)

  fun update(vatom: Vatom, handler: LoadHandler) {
    val temp = this.vatom
    onVatomChanged(vatom)
    this.vatom = vatom
    if (onVatomChanged(temp, vatom)) {
      onLoad(handler)
    } else
      handler.onComplete()
  }

  open fun onVatomChanged(vatom: Vatom) {}

  open fun onVatomChanged(oldVatom: Vatom, newVatom: Vatom): Boolean {
    return true
  }

  open fun onUnload() {
    cancel()
  }

  @Synchronized
  fun collect(cancellable: Cancellable) {
    this.cancellable.add(cancellable)
  }

  @Synchronized
  fun cancel() {
    cancellable.cancel()
    cancellable = CompositeCancellable()
  }

  interface LoadHandler {

    fun onComplete()

    fun onError(error: Throwable)

    fun collect(cancellable: Cancellable)
  }

}