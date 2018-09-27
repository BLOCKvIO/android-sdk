package io.blockv.face.client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.blockv.common.internal.net.rest.auth.ResourceEncoder
import io.blockv.common.model.Vatom
import io.blockv.common.util.Callable
import io.blockv.common.util.Cancellable
import io.blockv.common.util.CompositeCancellable
import io.blockv.face.R

class FaceManagerImpl(val resourceEncoder: ResourceEncoder, var resourceManager: ResourceManager) : FaceManager {

  private val factories: HashMap<String, ViewFactory> = HashMap()
  private var loader: FaceManager.ViewEmitter? = object : FaceManager.ViewEmitter {
    override fun emit(
      inflater: LayoutInflater,
      parent: ViewGroup,
      vatom: Vatom,
      resourceManager: ResourceManager
    ): View {
      return inflater.inflate(R.layout.view_basic_loader, parent, false)
    }
  }
  private var error: FaceManager.ViewEmitter? = object : FaceManager.ViewEmitter {
    override fun emit(
      inflater: LayoutInflater,
      parent: ViewGroup,
      vatom: Vatom,
      resourceManager: ResourceManager
    ): View {
      val layout = inflater.inflate(R.layout.view_vatom_error, parent, false) as ViewGroup
      val activated: ImageView = layout.findViewById(R.id.activated)
      val loader: View = layout.findViewById(R.id.loader)
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
            loader.visibility = View.VISIBLE
            cancellable.add(
              resourceManager.getBitmap(resource, parent.width, parent.height)
                .returnOn(Callable.Scheduler.MAIN)
                .call({
                  activated.setImageBitmap(it)
                  loader.visibility = View.GONE
                }, {
                  loader.visibility = View.GONE
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

  override val faceRoster: Map<String, ViewFactory>
    get() = factories

  override var defaultLoader: FaceManager.ViewEmitter?
    get() = loader
    set(value) {
      loader = value
    }
  override var defaultError: FaceManager.ViewEmitter?
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

      fun load(vatomView: VatomView, errorView: View?, loaderView: View?): Callable<FaceView> {
        return Callable.single {
          //unload previous face view
          vatomView.faceView?.onUnload()
          vatomView.faceView = null
          vatomView.showLoader(true, loaderDelay)
        }
          .runOn(Callable.Scheduler.MAIN)
          .returnOn(Callable.Scheduler.COMP)
          .map {
            val face = faceProcedure.select(vatom, faceRoster.keys)
              ?: throw FaceManager.Builder.Error.FACE_MODEL_IS_NULL.exception
            val factory = faceRoster[face.property.displayUrl]
              ?: throw FaceManager.Builder.Error.FACTORY_NOT_FOUND.exception
            Pair(face, factory)
          }
          .runOn(Callable.Scheduler.COMP)
          .returnOn(Callable.Scheduler.MAIN)
          .map {
            val view = it.second.emit(vatom, it.first, FaceBridge(resourceEncoder, resourceManager))
            vatomView.faceView = view
            view
          }
          .runOn(Callable.Scheduler.MAIN)
          .flatMap { faceView ->
            Callable.create<FaceView> { emitter ->
              val cancel = CompositeCancellable()
              faceView.onLoad(object : FaceView.LoadHandler {
                override fun collect(cancellable: Cancellable) {
                  cancel.add(cancellable)
                }

                override fun onComplete() {
                  emitter.onResult(faceView)
                  emitter.onComplete()
                  faceView.isLoaded = true
                }

                override fun onError(error: Throwable) {
                  emitter.onError(error)
                }
              })

              emitter.doOnCompletion {
                cancel.cancel()
              }
            }
          }
          .runOn(Callable.Scheduler.MAIN)

      }

      override fun into(vatomView: VatomView): Callable<FaceView> {

        val errorView: View? = this.errorView
        val loaderView: View? = this.loaderView
        val faceProcedure: FaceManager.FaceSelectionProcedure = this.faceProcedure

        synchronized(vatomView)
        {
          val faceView = vatomView.faceView

          return Callable.single {
            //setup
            val inflater = LayoutInflater.from(vatomView.context)
            vatomView.loaderView = loaderView ?: defaultLoader?.emit(inflater, vatomView, vatom, resourceManager)
            vatomView.errorView = errorView ?: defaultError?.emit(inflater, vatomView, vatom, resourceManager)

          }
            .runOn(Callable.Scheduler.MAIN)
            .flatMap {
              if (faceView?.isLoaded == true //only try update face view if its loaded
                && faceView.vatom.property.templateVariationId == vatom.property.templateVariationId
              ) {
                //update
                faceView.isLoaded = false
                Callable.single {
                  faceProcedure.select(vatom, faceRoster.keys)
                }
                  .runOn(Callable.Scheduler.COMP)
                  .returnOn(Callable.Scheduler.MAIN)
                  .flatMap { face ->
                    if (face != null && face.id == faceView.face.id) {
                      Callable.create { emitter ->
                        val cancel = CompositeCancellable()
                        faceView.update(vatom, object : FaceView.LoadHandler {
                          override fun collect(cancellable: Cancellable) {
                            cancel.add(cancellable)
                          }

                          override fun onComplete() {
                            emitter.onResult(faceView)
                            emitter.onComplete()
                            faceView.isLoaded = true
                          }

                          override fun onError(error: Throwable) {
                            emitter.onError(error)
                          }
                        })
                        emitter.doOnCompletion {
                          cancel.cancel()
                        }
                      }
                    } else {
                      load(vatomView, errorView, loaderView)
                    }
                  }
                  .runOn(Callable.Scheduler.MAIN)
                  .returnOn(Callable.Scheduler.MAIN)
              } else
                load(vatomView, errorView, loaderView)
            }
            .map {
              if (vatomView.faceView == it) {
                vatomView.showFaceView(true)
              } else
                throw FaceManager.Builder.Error.FACE_VIEW_CHANGED.exception//face view being displayed has changed
              it
            }
            .runOn(Callable.Scheduler.MAIN)
            .doOnError {
              if (it !is FaceManager.Builder.VatomViewException || it.error != FaceManager.Builder.Error.FACE_VIEW_CHANGED) {
                vatomView.showError(true)
                vatomView.faceView = null
              }
            }


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