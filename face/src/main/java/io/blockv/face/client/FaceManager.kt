package io.blockv.face.client

import android.view.View
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.common.util.Callable

interface FaceManager {

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
            faceRegistry: Set<String>
          ): Face? {
            var context: EmbeddedProcedure? = procedure
            do {
              val face = defaultRoutine(vatom.faces, faceRegistry, context!!.viewMode)
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

  fun registerFace(factory: ViewFactory)

  val faceRoster: Map<String, ViewFactory>

  var defaultLoader: ViewEmitter?

  var defaultError: ViewEmitter?

  interface FaceSelectionProcedure {
    fun select(vatom: Vatom, faceRegistry: Set<String>): Face?
  }

  fun load(vatom: Vatom): Builder

  interface Builder {

    fun into(vatomView: VatomView): Callable<FaceView>

    fun setEmbeddedProcedure(embeddedProcedure: EmbeddedProcedure): Builder

    fun setFaceSelectionProcedure(routine: FaceSelectionProcedure): Builder

    fun setErrorView(view: View): Builder

    fun setLoaderView(view: View): Builder

    fun setLoaderDelay(time: Long): Builder

    enum class Error(val message: String) {
      FACTORY_NOT_FOUND("The face's display url is not a registered native face"),
      FACE_MODEL_IS_NULL("The face selection procedure has returned null"),
      VATOM_VIEW_FACE_CHANGED("The face view being displayed in vatom view has been changed");

      val exception: Exception
        get() = VatomViewException(this)
    }

    class VatomViewException(val error: Error) : Exception(error.message)
  }

}