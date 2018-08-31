package io.blockv.face.client

import io.blockv.core.model.Face
import io.blockv.core.model.Vatom

interface ViewFactory {

  val displayUrl: String

  fun emit(vatom: Vatom, face: Face, bridge: FaceBridge?): FaceView

  companion object {

    fun wrap(displayUrl: String, emitter: (vatom: Vatom, face: Face, bridge: FaceBridge?) -> FaceView): ViewFactory {

      return object : ViewFactory {

        override fun emit(vatom: Vatom, face: Face, bridge: FaceBridge?): FaceView {
          return emitter.invoke(vatom, face, bridge)
        }

        override val displayUrl: String
          get() = displayUrl
      }
    }
  }

}