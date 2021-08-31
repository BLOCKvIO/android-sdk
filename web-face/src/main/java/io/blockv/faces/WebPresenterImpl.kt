package io.blockv.faces

import android.content.Intent
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import androidx.annotation.RequiresApi
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.face.client.FaceBridge
import io.blockv.face.client.FaceView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.json.JSONObject
import java.util.concurrent.TimeUnit

internal class WebPresenterImpl(
  val view: WebViewSpec,
  val faceBridge: FaceBridge,
  val face: Face,
  var vatom: Vatom
) : WebPresenter {

  @get:Synchronized
  @set:Synchronized
  var bridge: BaseBridge? = null

  @get:Synchronized
  @set:Synchronized
  var loadHandler: FaceView.LoadHandler? = null

  override fun onAlert(url: String, message: String, complete: () -> Unit): Boolean {
    view.showAlertDialog(vatom.property.title ?: "Alert", message, complete)
    return true
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun openLink(request: WebResourceRequest): Boolean {
    return openLink(request.url.toString())
  }

  override fun openLink(url: String): Boolean {
    if (url.startsWith("mailto:")) {
      val mail = MailTo.parse(url)
      val intent = Intent(Intent.ACTION_SEND)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      intent.type = "message/rfc822"
      intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mail.to))
      intent.putExtra(Intent.EXTRA_TEXT, mail.body)
      intent.putExtra(Intent.EXTRA_SUBJECT, mail.subject)
      intent.putExtra(Intent.EXTRA_CC, mail.cc)
      view.startActivityChooser("Send email using…", intent)

      return true
    } else if (url.startsWith("tel:")) {
      val intent = Intent(Intent.ACTION_DIAL)
      intent.data = Uri.parse(url)
      view.startActivityChooser("Open Using…", intent)
      return true
    }
    return false
  }

  override fun receiveMessage(data: String) {

    val message = Bridge.Message(JSONObject(data))

    if ((message.version == "1.0.0" && message.source.toLowerCase() != "vatom")
      || (message.version != "1.0.0" && message.source.toLowerCase() != "blockv_face_sdk")
    ) {
      throw Exception("Unsupported source - ${message.source}")
    }
    if (bridge == null) {

      bridge = when (message.version) {
        "1.0.0" -> {
          BridgeV1(faceBridge, vatom, face) { id, payload ->
            sendMessage(id, payload)
          }
        }
        "2.0.0" -> {
          BridgeV2(faceBridge, vatom, face) { id, payload ->
            sendMessage(id, payload)
          }
        }
        else -> {
          throw Exception("Unsupported version - ${message.version}")
        }
      }
    }
    bridge?.onMessage(message)

  }

  private fun sendMessage(id: String, payload: String) {
    view.sendMessage(id, payload)
  }

  override fun onPageLoaded() {
    loadHandler?.onComplete()
    loadHandler = null
  }

  override fun onLoad(handler: FaceView.LoadHandler) {
    this.loadHandler = handler
    Single.fromCallable {
      this.view.loadUrl(face.property.displayUrl)
    }.flatMap { Single.timer(3, TimeUnit.SECONDS) }
      .subscribeOn(AndroidSchedulers.mainThread())
      .subscribe({
        onPageLoaded()
      }, {
        onPageLoaded()
      })

  }

  override fun onVatomUpdated(vatom: Vatom) {
    bridge?.onVatomUpdate(vatom)
  }
}