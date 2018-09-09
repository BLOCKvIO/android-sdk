package io.blockv.face.client

import android.view.View
import io.blockv.common.model.Vatom
import io.blockv.common.util.Callable

class FaceManagerImpl : FaceManager {

  private val factories: HashMap<String, ViewFactory> = HashMap()

  override fun registerFace(factory: ViewFactory) {
    factories[factory.displayUrl] = factory
  }

  override val faceRegistry: Map<String, ViewFactory>
    get() = factories

  override fun load(vatom: Vatom): FaceManager.Builder {
    return object : FaceManager.Builder {

      var faceProcedure: FaceManager.FaceSelectionProcedure = FaceManager.EmbeddedProcedure.ICON.procedure
      var errorView: View? = null
      var loaderView: View? = null

      override fun into(vatomView: VatomView): Callable<FaceView> {

        val faces = vatom.faces
        val actions = vatom.actions
        val errorView: View? = this.errorView
        val loaderView: View? = this.loaderView
        val faceProcedure: FaceManager.FaceSelectionProcedure = this.faceProcedure

        return Callable.single {
          val face = faceProcedure.select(vatom, faceRegistry.keys)
            ?: throw FaceManager.Builder.Error.FACE_MODEL_IS_NULL.exception
          val factory = faceRegistry[face.property.displayUrl]
            ?: throw FaceManager.Builder.Error.FACTORY_NOT_FOUND.exception
          Pair(face, factory)
        }
          .runOn(Callable.Scheduler.COMP)
          .returnOn(Callable.Scheduler.MAIN)
          .map {
            vatomView.loaderView = loaderView
            vatomView.errorView = errorView
            vatomView.showLoader(true)
            val view = it.second.emit(vatom, it.first, FaceBridge())
            vatomView.faceView = view
            view
          }
          .runOn(Callable.Scheduler.MAIN)
          .map {
            it
          }
          .runOn(Callable.Scheduler.COMP)
          .map {
            vatomView.showVatomView(true)
            it
          }
          .runOn(Callable.Scheduler.MAIN)


      }

      override fun setEmbeddedProcedure(embeddedProcedure: FaceManager.EmbeddedProcedure): FaceManager.Builder {
        this.faceProcedure = embeddedProcedure.procedure
        return this
      }

      override fun setFaceSelectionProcedure(routine: FaceManager.FaceSelectionProcedure): FaceManager.Builder {
        this.faceProcedure = routine
        return this
      }

      override fun setErrorView(view: View): FaceManager.Builder {
        errorView = view
        return this
      }

      override fun setLoaderView(view: View): FaceManager.Builder {
        loaderView = view
        return this
      }
    }
  }
}