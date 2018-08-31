package io.blockv.face.client

import android.view.View
import io.blockv.core.model.Vatom

interface FaceView {

  fun onCreate(vatom: Vatom, bridge: FaceBridge): View

  fun onLoad(handler: LoadHandler)

  fun onVatomUpdate(vatom: Vatom)

  fun onUnload()

  interface LoadHandler {

    fun onComplete()

    fun onError(error: Throwable)
  }

}