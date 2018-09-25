package io.blockv.face.client

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.blockv.common.internal.net.rest.auth.ResourceEncoder
import io.blockv.common.model.Vatom
import io.blockv.common.util.Callable
import io.blockv.common.util.CompositeCancellable
import io.blockv.face.R

class FaceManagerImpl(val resourceEncoder: ResourceEncoder, var resourceManager: ResourceManager) : FaceManager {

  private val factories: HashMap<String, ViewFactory> = HashMap()
  private var loader: ViewEmitter? = object : ViewEmitter {
    override fun emit(
      inflater: LayoutInflater,
      parent: ViewGroup,
      vatom: Vatom,
      resourceManager: ResourceManager
    ): View {
      return inflater.inflate(R.layout.view_basic_loader, parent, false)
    }
  }
  private var error: ViewEmitter? = object : ViewEmitter {
    override fun emit(
      inflater: LayoutInflater,
      parent: ViewGroup,
      vatom: Vatom,
      resourceManager: ResourceManager
    ): View {
      val layout = inflater.inflate(R.layout.view_vatom_error, parent, false) as ViewGroup
      val activated: ImageView = layout.findViewById(R.id.activated)
      var cancellable = CompositeCancellable()
      val resource = vatom.property.getResource("ActivatedImage")

      layout.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

        override fun onViewDetachedFromWindow(v: View?) {
          cancellable.cancel()
          layout.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
          cancellable = CompositeCancellable()
          if (resource != null) {
            cancellable.add(
              resourceManager.getBitmap(resource, parent.width, parent.height)
                .returnOn(Callable.Scheduler.MAIN)
                .call({
                  activated.setImageBitmap(it)
                }, {

                })
            )
          }
        }

      })

      return layout
    }
  }

  override fun registerFace(factory: ViewFactory) {
    factories[factory.displayUrl] = factory
  }

  override val faceRegistry: Map<String, ViewFactory>
    get() = factories

  override var defaultLoader: ViewEmitter?
    get() = loader
    set(value) {
      loader = value
    }
  override var defaultError: ViewEmitter?
    get() = error
    set(value) {
      error = value
    }

  override fun load(vatom: Vatom): FaceManager.Builder {
    return object : FaceManager.Builder {

      var faceProcedure: FaceManager.FaceSelectionProcedure = FaceManager.EmbeddedProcedure.ICON.procedure
      var errorView: View? = null
      var loaderView: View? = null
      var loaderDelay: Long = 0

      override fun into(vatomView: VatomView): Callable<FaceView> {

        val errorView: View? = this.errorView
        val loaderView: View? = this.loaderView
        val faceProcedure: FaceManager.FaceSelectionProcedure = this.faceProcedure

        val inflater = LayoutInflater.from(vatomView.context)

        return Callable.single {
          vatomView.loaderView = loaderView ?: defaultLoader?.emit(inflater, vatomView, vatom, resourceManager)
          vatomView.errorView = errorView ?: defaultError?.emit(inflater, vatomView, vatom, resourceManager)
          vatomView.showLoader(true, loaderDelay)
        }
          .runOn(Callable.Scheduler.MAIN)
          .returnOn(Callable.Scheduler.COMP)
          .map {
            val face = faceProcedure.select(vatom, faceRegistry.keys)
              ?: throw FaceManager.Builder.Error.FACE_MODEL_IS_NULL.exception
            val factory = faceRegistry[face.property.displayUrl]
              ?: throw FaceManager.Builder.Error.FACTORY_NOT_FOUND.exception
            Pair(face, factory)
          }
          .runOn(Callable.Scheduler.COMP)
          .returnOn(Callable.Scheduler.MAIN)
          .map {

            Log.e("facemanager", "face ${it.first}")
            val view = it.second.emit(vatom, it.first, FaceBridge(resourceEncoder, resourceManager))
            vatomView.faceView = view
            view
          }
          .runOn(Callable.Scheduler.MAIN)
          .flatMap { faceView ->
            Callable.create<FaceView> { emitter ->
              faceView.onLoad(object : FaceView.LoadHandler {
                override fun onComplete() {
                  emitter.onResult(faceView)
                  emitter.onComplete()
                }

                override fun onError(error: Throwable) {
                  emitter.onError(error)
                }
              })
            }
          }
          .runOn(Callable.Scheduler.MAIN)
          .map {
            vatomView.showVatomView(true)
            it
          }
          .runOn(Callable.Scheduler.MAIN)
          .doOnError {
            vatomView.showError(true)
          }

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

      override fun setLoaderDelay(time: Long): FaceManager.Builder {
        loaderDelay = time
        return this
      }
    }
  }
}