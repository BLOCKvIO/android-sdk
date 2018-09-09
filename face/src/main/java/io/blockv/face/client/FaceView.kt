package io.blockv.face.client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom


abstract class FaceView(
  var vatom: Vatom,
  var face: Face,
  val bridge: FaceBridge
) {

  abstract fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View

  abstract fun onLoad(handler: LoadHandler)

  abstract fun onVatomUpdate(vatom: Vatom)

  abstract fun onUnload()

  interface LoadHandler {

    fun onComplete()

    fun onError(error: Throwable)
  }

}