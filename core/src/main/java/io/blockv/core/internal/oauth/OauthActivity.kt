package io.blockv.core.internal.oauth

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import io.blockv.common.model.OauthData
import io.blockv.core.R
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

class OauthActivity : AppCompatActivity() {

  companion object {

    fun start(
      context: Context,
      appId: String,
      redirectUri: String,
      scope: String,
      handler: Handler
    ) {
      val intent = Intent(context, OauthActivity::class.java)
      intent.putExtra("appId", appId)
      intent.putExtra("redirectUri", redirectUri)
      intent.putExtra("handlerId", gethandlerId(handler))
      intent.putExtra("scope", scope)
      context.startActivity(intent)
    }

    private var handlers = HashMap<Long, Handler>()

    private var complete = false

    private var handlerId: Long = 0
    @Synchronized
    private fun gethandlerId(handler: Handler): Long {
      val id = handlerId
      handlers[id] = handler
      handlerId += 1
      return id
    }

    @Synchronized
    internal fun getHandler(id: Long): Handler? {
      return handlers[id]
    }

    @Synchronized
    internal fun removeHandler(handlerId: Long) {
      handlers.remove(handlerId)
    }

  }

  lateinit var webView: WebView
  lateinit var loader: View
  lateinit var handler: Handler
  var handlerId: Long = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.blockv_oauth)
    webView = findViewById(R.id.webview)
    loader = findViewById(R.id.loader)
    handler = getHandler(intent.getLongExtra("handlerId", -1))!!
    val appId = intent.getStringExtra("appId")
    val redirectUri = intent.getStringExtra("redirectUri") ?: ""
    val scope = intent.getStringExtra("scope")
    val state = Uri.encode(UUID.randomUUID().toString())
    handlerId = intent.getLongExtra("handlerId", -1)

    val url =
      "https://login.blockv.io/?response_type=code&client_id=$appId&redirect_uri=${Uri.encode(redirectUri)}&scope=$scope&state=$state"
    webView.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith(redirectUri)) {
          val data = Uri.parse(url)
          if (data.getQueryParameter("state") != state) {
            handler.onError(BlockvOauthException.Error.STATE_CHANGED.exception())
          } else
            if (data.getQueryParameter("error") != null) {
              val error = data.getQueryParameter("error")
              if (error == "access_denied") {
                handler.onError(BlockvOauthException.Error.ACCESS_DENIED.exception())
              } else {
                handler.onError(BlockvOauthException.Error.UNKNOWN.exception())
                Log.e("oauth", error ?: "(null)")
              }
              complete()
            } else
              if (data.getQueryParameter("code") != null) {
                val flow = if (data.getQueryParameter("flow") != null) {
                  data.getQueryParameter("flow")
                } else {
                  "other"
                }
                handler.onSuccess(data.getQueryParameter("code") ?: "", flow ?: "")
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnSubscribe {
                    webView.visibility = View.GONE
                    loader.visibility = View.VISIBLE
                  }
                  .doFinally {
                    complete()
                  }
                  .subscribe()

              } else {
                handler.onError(BlockvOauthException.Error.UNKNOWN.exception())
                complete()
              }

          return true
        }
        return false
      }

      override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        loader.visibility = View.GONE
        webView.visibility = View.VISIBLE
      }

      override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        handler.onError(BlockvOauthException.Error.OAUTH_UNAVAILABLE.exception())
        complete()
      }

      @TargetApi(Build.VERSION_CODES.M)
      override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        super.onReceivedError(view, request, error)
        handler.onError(BlockvOauthException.Error.OAUTH_UNAVAILABLE.exception())
        complete()
      }

    }
    webView.settings.javaScriptEnabled = true
    webView.settings.domStorageEnabled = true
    webView.webChromeClient =
      object : WebChromeClient() {
        override fun onJsAlert(
          view: WebView,
          url: String,
          message: String,
          result: JsResult
        ): Boolean {
          super.onJsAlert(view, url, message, result)

          AlertDialog.Builder(view.context)
            .setCancelable(true)
            .setMessage(message)
            .setOnCancelListener { result.cancel() }
            .setPositiveButton("OK") { _, _ ->
              result.confirm()
            }
            .show()

          return true
        }
      }
    webView.loadUrl(url)
  }

  fun complete() {
    complete = true
    finish()
  }

  override fun finish() {
    super.finish()
    if (!complete) {
      handler.onError(BlockvOauthException.Error.USER_CANCEL.exception())
    }
    removeHandler(handlerId)
  }

  interface Handler {
    fun onSuccess(code: String, flow: String): Single<OauthData>

    fun onError(exception: BlockvOauthException)
  }
}