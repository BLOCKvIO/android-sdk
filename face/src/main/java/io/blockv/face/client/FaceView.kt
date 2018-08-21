package io.blockv.face.client

import android.content.Context
import android.view.View
import io.blockv.core.client.manager.EventManager
import io.blockv.core.client.manager.UserManager
import io.blockv.core.client.manager.VatomManager
import io.blockv.core.model.Action
import io.blockv.core.model.Face
import io.blockv.core.model.Vatom
import io.blockv.core.util.Callable

abstract class FaceView(
  val vatom: Vatom,
  val face: Face,
  val actions: List<Action>,
  val bridge: FaceBridge
) {
  var loaded = false
  val loadEmitters: HashSet<Callable.ResultEmitter<FaceView>> = HashSet()

  open fun onLoad() {
    //this on background
  }

  //call once view is ready to display
  internal fun loadComplete() {
    synchronized(loadEmitters) {
      loaded = true
      loadEmitters.forEach {
        it.onResult(this)
        it.onComplete()
      }
      loadEmitters.clear()
    }
  }

  abstract fun getView(context: Context): View //on main thread, must not block

  fun onLoadComplete(): Callable<FaceView> {
    return Callable.create {
      synchronized(loadEmitters) {
        if (loaded) {
          it.onResult(this)
          it.onComplete()
        } else {
          loadEmitters.add(it)
        }
      }
    }
  }

  open fun onUnload() {

  }

}