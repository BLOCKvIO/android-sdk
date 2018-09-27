package io.blockv.face.client

import io.blockv.common.model.Face
import io.blockv.common.model.Vatom

/**
 * Used to emit custom FaceViews. Each custom FaceView should have it's own factory.
 */
interface ViewFactory {

  /**
   * The display url of the FaceView, used when registering and selecting a ViewFactory.
   *
   * @see FaceManager.registerFace
   */
  val displayUrl: String

  /**
   * Emits a new instance of a custom FaceView.
   *
   * @param vatom to be displayed.
   * @param face model that was selected.
   * @param bridge provides available sdk functions to the FaceView.
   * @return new FaceView
   */
  fun emit(
    vatom: Vatom,
    face: Face,
    bridge: FaceBridge
  ): FaceView

  companion object {

    fun wrap(
      displayUrl: String, emitter: (
        vatom: Vatom,
        face: Face,
        bridge: FaceBridge
      ) -> FaceView
    ): ViewFactory {

      return object : ViewFactory {

        override fun emit(
          vatom: Vatom,
          face: Face,
          bridge: FaceBridge
        ): FaceView {
          return emitter.invoke(vatom, face, bridge)
        }

        override val displayUrl: String
          get() = displayUrl
      }
    }
  }

}