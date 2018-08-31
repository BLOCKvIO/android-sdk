package io.blockv.face.client

import io.blockv.core.model.Vatom

interface ViewFactory {

  val displayUrl: String

  fun emit(vatom: Vatom, bridge: FaceBridge?): FaceView

  companion object {

    fun wrap(displayUrl: String, emitter: (vatom: Vatom, bridge: FaceBridge?) -> FaceView): ViewFactory {

      return object : ViewFactory {

        override fun emit(vatom: Vatom, bridge: FaceBridge?): FaceView {
          return emitter.invoke(vatom, bridge)
        }

        override val displayUrl: String
          get() = displayUrl
      }
    }
  }

}