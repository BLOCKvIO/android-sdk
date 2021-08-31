package io.blockv.faces

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import java.io.File

@SuppressLint("SetJavaScriptEnabled")
internal class WebViewSpecImpl(val webView: WebFace.CustomWebView) :
  WebViewSpec {

  init {
    val cache = File(webView.context.cacheDir, "web-face")
    val webSettings = webView.settings
    webSettings.setAppCachePath(cache.absolutePath)
    webSettings.javaScriptEnabled = true
    webSettings.databaseEnabled = true
    webSettings.domStorageEnabled = true
    webSettings.allowFileAccess = true
    webSettings.cacheMode = WebSettings.LOAD_DEFAULT
    webView.isVerticalScrollBarEnabled = false
  }

  override fun registerPresenter(webPresenter: WebPresenter) {
    webView.webViewClient = object : WebViewClient() {

      override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.cancel()
      }

      override fun onPageFinished(view: WebView, url: String) {
        webPresenter.onPageLoaded()
      }

      @TargetApi(Build.VERSION_CODES.N)
      override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return webPresenter.openLink(request)
      }

      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return webPresenter.openLink(url)
      }

    }
    webView.webChromeClient = object : WebChromeClient() {
      override fun onJsAlert(view: WebView, url: String, message: String, result: android.webkit.JsResult): Boolean {
        return webPresenter.onAlert(url, message) {
          result.confirm()
        }
      }

    }
    webView.removeJavascriptInterface("androidBridge")
    webView.addJavascriptInterface(BridgeInterface(webPresenter), "androidBridge")
  }

  override fun sendMessage(name: String, data: String) {
    webView.evaluateJavascript(
      "javascript:(window.vatomicEventReceiver||window.blockvEventReceiver).emit('message','$name',$data)",
      null
    )
  }

  override fun showAlertDialog(title: String, message: String, handler: () -> Unit) {
    try {
      AlertDialog.Builder(webView.context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(
          android.R.string.ok
        ) { _, _ -> handler.invoke() }
        .setCancelable(false).create().show()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun loadUrl(url: String) {
    webView.loadUrl(url)
  }

  override fun startActivityChooser(message: String, intent: Intent) {
    webView.context.startActivity(
      Intent.createChooser(
        intent,
        message
      )
    )
  }

  internal inner class BridgeInterface(val webPresenter: WebPresenter) {
    @JavascriptInterface
    fun onIncomingBridgeMessage(event: String) {
      webPresenter.receiveMessage(event)
    }
  }
}