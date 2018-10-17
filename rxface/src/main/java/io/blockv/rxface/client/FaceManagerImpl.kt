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
import android.widget.ImageView
import io.blockv.common.model.Vatom
import io.blockv.common.util.Cancellable
import io.blockv.common.util.CompositeCancellable
import io.blockv.face.R
import io.blockv.face.client.FaceBridge
import io.blockv.face.client.FaceManager.EmbeddedProcedure
import io.blockv.face.client.FaceManager.FaceSelectionProcedure
import io.blockv.face.client.FaceView
import io.blockv.face.client.VatomView
import io.blockv.face.client.ViewFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FaceManagerImpl(var resourceManager: ResourceManager) :
  FaceManager {

  private val factories: HashMap<String, ViewFactory> = HashMap()
  private var loader: FaceManager.ViewEmitter? = object :
    FaceManager.ViewEmitter {
    override fun emit(
      inflater: LayoutInflater,
      parent: ViewGroup,
      vatom: Vatom,
      resourceManager: ResourceManager
    ): View {
      return inflater.inflate(R.layout.view_basic_loader, parent, false)
    }
  }
  private var error: FaceManager.ViewEmitter? = object :
    FaceManager.ViewEmitter {
    override fun emit(
      inflater: LayoutInflater,
      parent: ViewGroup,
      vatom: Vatom,
      resourceManager: ResourceManager
    ): View {
      val layout = inflater.inflate(R.layout.view_vatom_error, parent, false) as ViewGroup
      val activated: ImageView = layout.findViewById(R.id.activated)
      val loader: View = layout.findViewById(R.id.loader)
      var disposable = CompositeDisposable()
      val resource = vatom.property.getResource("ActivatedImage")

      layout.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

        override fun onViewDetachedFromWindow(v: View?) {
          disposable.dispose()
          layout.removeOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View?) {
          disposable = CompositeDisposable()
          if (resource != null) {
            loader.visibility = View.VISIBLE
            disposable.add(
              resourceManager.getBitmap(resource, parent.width, parent.height)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
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

      var faceProcedure: FaceSelectionProcedure = EmbeddedProcedure.ICON.procedure
      var errorView: View? = null
      var loaderView: View? = null
      var loaderDelay: Long = 0

      fun load(vatomView: VatomView): Single<FaceView> {
        return Single.fromCallable {
          //unload previous face view
          vatomView.faceView?.onUnload()
          vatomView.faceView = null
          vatomView.showLoader(true, loaderDelay)
        }
          .subscribeOn(AndroidSchedulers.mainThread())
          .observeOn(Schedulers.computation())
          .map {
            val face = faceProcedure.select(vatom, faceRoster.keys)
              ?: throw FaceManager.Builder.Error.FACE_MODEL_IS_NULL.exception
            val factory = faceRoster[face.property.displayUrl]
              ?: throw FaceManager.Builder.Error.FACTORY_NOT_FOUND.exception
            Pair(face, factory)
          }
          .subscribeOn(Schedulers.computation())
          .observeOn(AndroidSchedulers.mainThread())
          .map {
            val view = it.second.emit(vatom, it.first, FaceBridge(
              ResourceManagerWrapper(
                resourceManager
              )
            ))
            vatomView.faceView = view
            view
          }
          .subscribeOn(AndroidSchedulers.mainThread())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap { faceView ->
            val cancel = CompositeCancellable()
            Single.create<FaceView> { emitter ->
              faceView.onLoad(object : FaceView.LoadHandler {
                override fun collect(cancellable: Cancellable) {
                  cancel.add(cancellable)
                }

                override fun onComplete() {
                  emitter.onSuccess(faceView)
                  faceView.isLoaded = true
                }

                override fun onError(error: Throwable) {
                  emitter.onError(error)
                }
              })
            }
              .doFinally {
                cancel.cancel()
              }
          }
          .observeOn(AndroidSchedulers.mainThread())

      }

      override fun into(vatomView: VatomView): Single<FaceView> {

        val errorView: View? = this.errorView
        val loaderView: View? = this.loaderView
        val faceProcedure: FaceSelectionProcedure = this.faceProcedure

        synchronized(vatomView)
        {
          val faceView = vatomView.faceView

          return Single.fromCallable {
            //setup
            val inflater = LayoutInflater.from(vatomView.context)
            vatomView.loaderView = loaderView ?: defaultLoader?.emit(inflater, vatomView, vatom, resourceManager)
            vatomView.errorView = errorView ?: defaultError?.emit(inflater, vatomView, vatom, resourceManager)

          }
            .subscribeOn(AndroidSchedulers.mainThread())
            .flatMap {
              if (faceView?.isLoaded == true //only try update face view if its loaded
                && faceView.vatom.property.templateVariationId == vatom.property.templateVariationId
              ) {
                //update
                faceView.isLoaded = false
                Single.fromCallable {
                  Optional(faceProcedure.select(vatom, faceRoster.keys))
                }
                  .subscribeOn(Schedulers.computation())
                  .observeOn(AndroidSchedulers.mainThread())
                  .flatMap { face ->
                    if (!face.isEmpty() && face.value?.id == faceView.face.id) {
                      val cancel = CompositeCancellable()
                      Single.create<FaceView> { emitter ->
                        faceView.update(vatom, object : FaceView.LoadHandler {
                          override fun collect(cancellable: Cancellable) {
                            cancel.add(cancellable)
                          }

                          override fun onComplete() {
                            emitter.onSuccess(faceView)
                            faceView.isLoaded = true
                          }

                          override fun onError(error: Throwable) {
                            emitter.onError(error)
                          }
                        })

                      }.doFinally {
                        cancel.cancel()
                      }

                    } else {
                      load(vatomView)
                    }
                  }
                  .subscribeOn(AndroidSchedulers.mainThread())
                  .observeOn(AndroidSchedulers.mainThread())
              } else
                load(vatomView)
            }
            .map {
              if (vatomView.faceView == it) {
                vatomView.showFaceView(true)
              } else
                throw FaceManager.Builder.Error.FACE_VIEW_CHANGED.exception//face view being displayed has changed
              it
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnError {
              if (it !is FaceManager.Builder.VatomViewException || it.error != FaceManager.Builder.Error.FACE_VIEW_CHANGED) {
                vatomView.showError(true)
                vatomView.faceView = null
              }
            }


        }


      }


      override fun setEmbeddedProcedure(embeddedProcedure: EmbeddedProcedure): FaceManager.Builder {
        this.faceProcedure = embeddedProcedure.procedure
        return this
      }

      override fun setFaceSelectionProcedure(procedure: FaceSelectionProcedure): FaceManager.Builder {
        this.faceProcedure = procedure
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