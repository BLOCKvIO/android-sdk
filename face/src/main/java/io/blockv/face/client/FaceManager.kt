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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.common.util.Callable
import io.blockv.face.client.FaceManager.EmbeddedProcedure.*

interface FaceManager {

  /**
   * EmbeddedProcedure enum contains a set of default face selection procedures.
   *
   * By default these procedures will only consider native faces that have a platform value 'Generic'
   * or 'Android'.
   *
   * Faces with a platform value of 'Android' will be selected over 'Generic' in the case where there are
   * multiple faces per view mode.
   *
   * @see ICON - Selects a Face model in icon mode, returns null if none are available.
   * @see ENGAGED - Selects a Face model in engaged mode, falls back on the ICON procedure if none are available.
   * @see FULLSCREEN - Selects a Face model in fullscreen mode, returns null if none are available.
   * @see CARD - Selects a Face model in card mode, returns null if none are available.
   *
   * @see FaceSelectionProcedure
   */
  enum class EmbeddedProcedure(val viewMode: String, val fallback: EmbeddedProcedure?) {

    ICON("icon", null),
    ENGAGED("engaged", ICON),
    FULLSCREEN("fullscreen", null),
    CARD("card", null);

    val procedure: FaceSelectionProcedure
      get() {
        val procedure = this
        return object : FaceManager.FaceSelectionProcedure {
          override fun select(
            vatom: Vatom,
            displayUrls: Set<String>
          ): Face? {
            var context: EmbeddedProcedure? = procedure
            do {
              val face = defaultRoutine(vatom.faces, displayUrls, context!!.viewMode)
              if (face != null)
                return face
              context = context.fallback
            } while (context != null)
            return null
          }
        }
      }

    companion object {
      val defaultRoutine: (faces: List<Face>, faceRegistry: Set<String>, viewMode: String) -> Face? =
        { faces, faceRegistry, viewMode ->
          var selectedFace: Face? = null
          var rating = 0
          for (face in faces) {

            if (face.property.viewMode != viewMode) {
              continue
            }
            //possibly turn into helper function isAndroid, isGeneric, isSupported
            var rate = when (face.property.platform.toLowerCase()) {
              "android" -> {
                2
              }
              "generic" -> {
                1
              }
              else -> {
                -1
              }
            }
            if (rate == -1) continue

            if (face.isNative()) {
              //check that the face is registered
              if (faceRegistry.indexOf(face.property.displayUrl) == -1) {
                continue
              }
              rate += 1
            }

            if (rate > rating) {
              rating = rate
              selectedFace = face
            }
          }
          selectedFace
        }
    }
  }

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
   * FaceSelectionProcedure is used to select a Face for a vAtom.
   *
   * @see EmbeddedProcedure
   */
  interface FaceSelectionProcedure {

    /**
     * Selects a Face model for a vAtom base on some custom logic.
     *
     * @param vatom contains the list of Face models that should be used by the procedure.
     * @param displayUrls is a set of all available FaceView factories.
     * @return an optional Face model.
     *
     * @see Vatom
     * @see Vatom.faces
     * @see Face
     */
    fun select(vatom: Vatom, displayUrls: Set<String>): Face?
  }

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

    val errorView: View?
    val loaderView: View?
    val loaderDelay: Long
    val faceProcedure: FaceSelectionProcedure

    /**
     * Builds a Callable to load a FaceView into the provided VatomView.
     *
     * @param vatomView to load a FaceView for.
     * @return new Callable<FaceView>, calling this will begin the loading chain.
     *
     * @see FaceView
     * @see VatomView
     */
    fun into(vatomView: VatomView): Callable<FaceView>

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
    fun setErrorView(view: View?): Builder

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
    fun setLoaderView(view: View?): Builder

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