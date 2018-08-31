package io.blockv.face.client

interface ViewFactory {

  val displayUrl: String

  fun emit(): FaceView

  companion object {

    fun wrap(displayUrl: String, emitter: () -> FaceView): ViewFactory {

      return object : ViewFactory {

        override fun emit(): FaceView {
          return emitter.invoke()
        }

        override val displayUrl: String
          get() = displayUrl
      }
    }
  }

}