package io.blockv.faces

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.face.client.FaceBridge
import io.blockv.face.client.FaceView
import io.blockv.face.client.ViewFactory

class WebFace(
  vatom: Vatom,
  face: Face,
  bridge: FaceBridge
) : FaceView(vatom, face, bridge) {

  lateinit var webView: CustomWebView
  internal lateinit var presenter: WebPresenter
  internal lateinit var view: WebViewSpec

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    webView = CustomWebView(inflater.context)
    view = WebViewSpecImpl(webView)
    presenter = WebPresenterImpl(view, bridge, face, vatom)
    view.registerPresenter(presenter)
    return webView
  }

  override fun onLoad(handler: LoadHandler) {
    presenter.onLoad(handler)
  }

  override fun onVatomChanged(oldVatom: Vatom, newVatom: Vatom): Boolean {
    if (oldVatom.id == newVatom.id) {
      presenter.onVatomUpdated(newVatom)
      return false
    } else
      return true
  }

  class CustomWebView : android.webkit.WebView {

    @get:Synchronized
    @set:Synchronized
    var overScrollListener: ((scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
      super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
      overScrollListener?.invoke(scrollX, scrollY, clampedX, clampedY)
    }

  }

  companion object {

    val factory: ViewFactory
      get() {
        return ViewFactory.wrap("https://*") { vatom, face, bridge ->
          WebFace(vatom, face, bridge)
        }
      }
  }
}