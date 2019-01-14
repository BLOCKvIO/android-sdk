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
import android.widget.ImageView
import io.blockv.common.model.Vatom
import io.blockv.common.util.Optional
import io.blockv.face.R
import io.blockv.face.client.manager.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FaceManagerImpl(
  var resourceManager: ResourceManager,
  var userManager: UserManager,
  var vatomManager: VatomManager,
  var eventManager: EventManager,
  var jsonSerializer: JsonSerializer
) : FaceManager {

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

      override var faceProcedure: FaceManager.FaceSelectionProcedure = FaceManager.EmbeddedProcedure.ICON.procedure
      override var errorView: View? = null
      override var loaderView: View? = null
      override var loaderDelay: Long = 0

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
            val factory = faceRoster[if (face.isNative()) face.property.displayUrl else "https://*"]
              ?: throw FaceManager.Builder.Error.FACTORY_NOT_FOUND.exception
            Pair(face, factory)
          }
          .observeOn(AndroidSchedulers.mainThread())
          .map {
            val view =
              it.second.emit(
                vatom, it.first, FaceBridge(
                  resourceManager,
                  userManager,
                  vatomManager,
                  eventManager,
                  jsonSerializer
                )
              )
            vatomView.faceView = view
            view
          }
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap { faceView ->
            Single.create<FaceView> { emitter ->
              val disposables = CompositeDisposable()
              faceView.onLoad(object : FaceView.LoadHandler {
                override fun collect(disposable: Disposable) {
                  disposables.add(disposable)
                }

                override fun onComplete() {
                  if (!emitter.isDisposed) {
                    emitter.onSuccess(faceView)
                    faceView.isLoaded = true
                  }
                }

                override fun onError(error: Throwable) {
                  if (!emitter.isDisposed) {
                    emitter.onError(error)
                  }
                }
              })
              emitter.setDisposable(disposables)
            }
              .subscribeOn(Schedulers.io())
          }
          .observeOn(AndroidSchedulers.mainThread())

      }

      override fun into(vatomView: VatomView): Single<FaceView> {

        val errorView: View? = this.errorView
        val loaderView: View? = this.loaderView
        val faceProcedure: FaceManager.FaceSelectionProcedure = this.faceProcedure

        synchronized(vatomView)
        {
          val faceView = vatomView.faceView

          return Single.fromCallable {
            //setup
            val inflater = LayoutInflater.from(vatomView.context)
            vatomView.loaderView = loaderView ?: defaultLoader?.emit(inflater, vatomView, vatom, resourceManager)
            vatomView.errorView = errorView ?: defaultError?.emit(inflater, vatomView, vatom, resourceManager)

            faceView?.isLoaded == true //only try update face view if its loaded
              && faceView.vatom.property.templateVariationId == vatom.property.templateVariationId
          }
            .subscribeOn(AndroidSchedulers.mainThread())
            .flatMap { update ->
              if (update) {
                Single.fromCallable {
                  Optional(faceProcedure.select(vatom, faceRoster.keys))
                }
                  .subscribeOn(Schedulers.computation())
                  .observeOn(AndroidSchedulers.mainThread())
                  .flatMap { face ->
                    if (!face.isEmpty() && face.value?.id == faceView?.face?.id) {
                      Single.create { emitter ->
                        val disposables = CompositeDisposable()
                        emitter.setDisposable(disposables)

                        faceView?.update(vatom, object : FaceView.LoadHandler {
                          override fun collect(disposable: Disposable) {
                            disposables.add(disposable)
                          }

                          override fun onComplete() {
                            if (!emitter.isDisposed) {
                              emitter.onSuccess(faceView)
                              faceView.isLoaded = true
                            }
                          }

                          override fun onError(error: Throwable) {
                            if (!emitter.isDisposed) {
                              emitter.onError(error)
                            }
                          }
                        })
                      }
                    } else {
                      faceView?.isLoaded = false
                      load(vatomView)
                    }
                  }

              } else
                load(vatomView)
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
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


      override fun setEmbeddedProcedure(embeddedProcedure: FaceManager.EmbeddedProcedure): FaceManager.Builder {
        this.faceProcedure = embeddedProcedure.procedure
        return this
      }

      override fun setFaceSelectionProcedure(procedure: FaceManager.FaceSelectionProcedure): FaceManager.Builder {
        this.faceProcedure = procedure
        return this
      }

      override fun setErrorView(view: View?): FaceManager.Builder {
        errorView = view
        return this
      }

      override fun setLoaderView(view: View?): FaceManager.Builder {
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