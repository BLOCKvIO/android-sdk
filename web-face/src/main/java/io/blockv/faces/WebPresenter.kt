package io.blockv.faces

import android.webkit.WebResourceRequest
import io.blockv.common.model.Vatom
import io.blockv.face.client.FaceView

internal interface WebPresenter {

  fun onAlert(url: String, message: String, complete: () -> Unit): Boolean

  fun openLink(request: WebResourceRequest): Boolean

  fun openLink(url: String): Boolean

  fun receiveMessage(data: String)

  fun onPageLoaded()

  fun onLoad(handler: FaceView.LoadHandler)

  fun onVatomUpdated(vatom: Vatom)
}