package io.blockv.face.client

import android.view.View
import io.blockv.core.model.Action
import io.blockv.core.model.Face
import io.blockv.core.model.Vatom

interface FaceViewFactory<T : View, FaceView> {

  val displayUrl: String

  fun emitFaceView(vatom: Vatom, face: Face, actions: List<Action>, bridge: FaceBridge?): T

}