package io.blockv.faces

import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.face.client.FaceBridge
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.json.JSONObject

internal abstract class BaseBridge(
  val faceBridge: FaceBridge,
  val sendMessage: (id: String, payload: String) -> Unit,
  var vatom: Vatom,
  val face: Face
) : Bridge {

  val disposable = CompositeDisposable()

  @Synchronized
  fun collect(disposable: Disposable) {
    this.disposable.add(disposable)
  }

  @Synchronized
  fun cancel() {
    this.disposable.clear()
  }
}