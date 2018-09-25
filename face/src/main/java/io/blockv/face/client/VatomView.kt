package io.blockv.face.client

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import java.util.*

class VatomView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
  FrameLayout(context, attrs, defStyleAttr) {

  constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)

  constructor(context: Context?) : this(context, null)


  private var loader: View? = null
  var loaderView: View?
    set(value) {
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
    set(value) {
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
  private var loaderDelayTimer: Timer? = null

  var faceView: FaceView?
    set(value) {
      if (view != null) {
        this.removeView(view)
        view = null
      }
      faceCode = value
      if (value != null) {
        val fView = value.onCreateView(LayoutInflater.from(context), this)
        fView.alpha = 0.000000001f
        view = fView
        addView(fView)
      }
    }
    get() {
      return faceCode
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
        removeView(loader)
      }
    }
  }

  @Synchronized
  fun showLoader(show: Boolean) {

    if (loaderDelayTimer != null) {
      loaderDelayTimer?.cancel()
      loaderDelayTimer = null
    }

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
      if (loaderDelayTimer != null) {
        loaderDelayTimer?.cancel()
      }
      loaderDelayTimer = Timer()
      loaderDelayTimer?.schedule(object : TimerTask() {
        override fun run() {
          val handler = Handler(Looper.getMainLooper())
          handler.post {
            if (loaderDelayTimer != null) {
              showLoader(show)
            }
          }
        }
      }, delay)
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

}