package io.blockv.faces

import android.content.Intent

internal interface WebViewSpec {

  fun registerPresenter(webPresenter: WebPresenter)

  fun showAlertDialog(title: String, message: String, handler: () -> Unit)

  fun startActivityChooser(message: String, intent: Intent)

  fun sendMessage(name: String, data: String)

  fun loadUrl(url: String)
}