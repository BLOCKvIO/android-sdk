package io.blockv.face.client

import android.content.Context
import android.view.View
import io.blockv.core.model.Face
import io.blockv.core.model.Vatom

interface FaceView {

  fun onCreate(context: Context, vatom: Vatom, face: Face, bridge: FaceBridge): View

  fun onLoad(handler: LoadHandler)

  fun onVatomUpdate(vatom: Vatom)

  fun onUnload()

  interface LoadHandler {

    fun onComplete()

    fun onError(error: Throwable)
  }

}