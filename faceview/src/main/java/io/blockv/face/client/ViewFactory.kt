/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.face.client

import io.blockv.common.model.Face
import io.blockv.common.model.Vatom

/**
 * Used to emit custom FaceViews. Each custom FaceView should have it's own factory.
 */
interface ViewFactory {

  /**
   * The display url of the FaceView, used when registering and selecting a ViewFactory.
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