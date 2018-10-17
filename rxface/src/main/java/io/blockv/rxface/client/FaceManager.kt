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
package io.blockv.rxface.client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.blockv.common.model.Vatom
import io.blockv.face.client.FaceManager.*
import io.blockv.face.client.FaceView
import io.blockv.face.client.VatomView
import io.blockv.face.client.ViewFactory
import io.reactivex.Single

interface FaceManager {

  /**
   * Adds a FaceView factory to the roster. If a factory with the same display url exists it
   * will be overwritten.
   *
   * @param factory is responsible for emitting a FaceView for a specific display url.
   * @see FaceView
   * @see ViewFactory
   */
  fun registerFace(factory: ViewFactory)

  /**
   * A map of all available FaceView factories.
   *
   * @see ViewFactory
   */
  val faceRoster: Map<String, ViewFactory>

  /**
   * The default loading view to be used by the VatomView.
   *
   * @see ViewEmitter
   */
  var defaultLoader: ViewEmitter?

  /**
   * The default error view to be used by the VatomView.
   *
   * @see ViewEmitter
   */
  var defaultError: ViewEmitter?

  /**
   * Creates a builder to load a VatomView for the specified vAtom.
   *
   * @param vatom is the vAtom you want a VatomView to display.
   * @return a new Builder.
   *
   * @see Builder
   */
  fun load(vatom: Vatom): Builder

  /**
   * Builder used to create a Callable to load a VatomView.
   */
  interface Builder {

    /**
     * Builds a Callable to load a FaceView into the provided VatomView.
     *
     * @param vatomView to load a FaceView for.
     * @return new Single<FaceView>, subscribing to this will begin the loading chain.
     *
     * @see FaceView
     * @see VatomView
     */
    fun into(vatomView: VatomView): Single<FaceView>

    /**
     * Sets an embedded procedure to be used to selected a Face model for the vAtom.
     *
     * @param embeddedProcedure to be used to select a face.
     * @return this Builder.
     *
     * @see EmbeddedProcedure
     */
    fun setEmbeddedProcedure(embeddedProcedure: EmbeddedProcedure): Builder

    /**
     * Set a custom procedure to be used to select a Face model for the vAtom.
     *
     * @param procedure to be used to select the Face model.
     * @return this Builder.
     *
     * @see FaceSelectionProcedure
     */
    fun setFaceSelectionProcedure(procedure: FaceSelectionProcedure): Builder

    /**
     * Sets the error view to be used by the VatomView. If this is not set
     * the default error view will be used.
     *
     * @param view to be used as the error view.
     * @return this Builder.
     *
     * @see VatomView
     * @see defaultError
     */
    fun setErrorView(view: View): Builder

    /**
     * Sets the loader view to be used by the VatomView. If this is not set
     * the default loader view will be used.
     *
     * @param view to be used as a loader view.
     * @return this Builder.
     *
     * @see VatomView
     * @see defaultLoader
     */
    fun setLoaderView(view: View): Builder

    /**
     * Sets a delay before the loader be will shown. If the VatomView finishes loading before
     * the timeout, the loader view will not be shown.
     *
     * @param time is the delay in milliseconds.
     * @return this Builder.
     */
    fun setLoaderDelay(time: Long): Builder

    enum class Error(val message: String) {
      FACTORY_NOT_FOUND("The face's display url is not a registered native face"),
      FACE_MODEL_IS_NULL("The face selection procedure has returned null"),
      FACE_VIEW_CHANGED("The face view being displayed has been changed");

      val exception: Exception
        get() = VatomViewException(this)
    }

    class VatomViewException(val error: Error) : Exception(error.message)
  }

  /**
   * View Emitter is used to create custom loader and error views.
   */
  interface ViewEmitter {

    /**
     * Creates a view to be used by VatomView.
     *
     * Do not attach the view to the parent, this is done during VatomView loading process.
     *
     * When using the LayoutInflater set 'attachedToRoot' false, inflater.inflate(<your-resource-id>, parent, false).
     */
    fun emit(inflater: LayoutInflater, parent: ViewGroup, vatom: Vatom, resourceManager: ResourceManager): View
  }
}