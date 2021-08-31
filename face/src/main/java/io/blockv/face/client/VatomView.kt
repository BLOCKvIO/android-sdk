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

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * VatomView is used to display a FaceView for a vAtom.
 *
 * You should not directly interact this view but rather use the functions provided by the
 * FaceManager.
 *
 * @see FaceManager
 */
class VatomView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
  FrameLayout(context, attrs, defStyleAttr), View.OnAttachStateChangeListener {

  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)

  constructor(context: Context) : this(context, null)


  init {
    this.addOnAttachStateChangeListener(this)
  }

  private var loader: View? = null
  var loaderView: View?
    @Synchronized set(value) {
      if (this.loader?.parent != null) {
        removeView(this.loader)
        if (value != null) {
          this.addView(value)
        }
      }
      loader = value
    }
    get() {
      return loader
    }

  private var error: View? = null
  var errorView: View?
    @Synchronized set(value) {
      if (this.error?.parent != null) {
        removeView(this.error)
        if (value != null) {
          this.addView(value)
        }
      }
      error = value
    }
    get() {
      return error
    }

  private var faceCode: FaceView? = null
  private var view: View? = null

  @Volatile
  private var loaderDelay: Disposable? = null


  @get:Synchronized
  val faceView: FaceView?
    get() {
      return faceCode
    }

  private var layoutEmitter: SingleEmitter<Unit>? = null

  fun loadFaceView(faceView: FaceView?): Single<Unit> {
    return Single.create<Unit> { emitter ->
      synchronized(this)
      {
        if (layoutEmitter?.isDisposed == false) {
          layoutEmitter?.onError(Throwable("faceview changed"))
        }
        if (view != null) {
          this.removeView(view)
          view = null
        }
        faceCode = faceView
        if (faceView != null) {
          layoutEmitter = emitter
          val fView = faceView.onCreateView(LayoutInflater.from(context), this)
          val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (!emitter.isDisposed) {
              emitter.onSuccess(Unit)
            }
          }
          val layoutListener =
            OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
              if (!emitter.isDisposed) {
                emitter.onSuccess(Unit)
              }
            }

          fView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
          fView.addOnLayoutChangeListener(layoutListener)
          fView.alpha = 0.000000001f
          emitter.setCancellable {
            fView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            fView.removeOnLayoutChangeListener(layoutListener)
          }
          view = fView
          addView(fView)
        } else {
          emitter.onSuccess(Unit)
        }
      }
    }
      .subscribeOn(AndroidSchedulers.mainThread())
  }

  @Synchronized
  fun showError(show: Boolean) {
    if (show) {
      showLoader(false)
      showFaceView(false)
      if (error != null && error?.parent == null) {
        addView(error)
      }
    } else {
      if (error != null && error?.parent != null) {
        removeView(error)
      }
    }
  }

  @Synchronized
  fun showLoader(show: Boolean) {

    loaderDelay?.dispose()

    if (show) {
      showError(false)
      showFaceView(false)
      if (loader != null && loader?.parent == null) {
        addView(loader)
      }
    } else {
      if (loader != null && loader?.parent != null) {
        removeView(loader)
      }
    }
  }

  @Synchronized
  fun showLoader(show: Boolean, delay: Long) {
    if (delay > 0) {
      loaderDelay?.dispose()
      loaderDelay = Observable.timer(delay, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          showLoader(show)
        }, {})
    } else
      showLoader(show)
  }


  @Synchronized
  fun showFaceView(show: Boolean) {

    if (show) {
      showError(false)
      showLoader(false)
      if (view != null) {
        view?.alpha = 1f
      }
    } else {
      if (view != null) {
        view?.alpha = 0.00000001f
      }
    }
  }

  fun isFaceViewVisibile(): Boolean {
    return view?.alpha == 1f
  }


  override fun onViewDetachedFromWindow(v: View?) {
    faceView?.onUnload()
  }

  override fun onViewAttachedToWindow(v: View?) {
  }
}