package io.blockv.face.client

import io.blockv.common.model.Face
import io.blockv.common.model.Vatom

interface FaceManager {

  enum class EmbeddedProcedure(val viewMode: String, val fallback: EmbeddedProcedure?) {

    ICON("icon", null),
    ACTIVATED("activated", ICON),
    FULLSCREEN("fullscreen", ICON),
    CARD("card", ICON);

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

  val faceRegistry: Map<String, ViewFactory>

  interface FaceSelectionProcedure {
    fun select(vatom: Vatom, faceRegistry: Set<String>): Face?
  }
}