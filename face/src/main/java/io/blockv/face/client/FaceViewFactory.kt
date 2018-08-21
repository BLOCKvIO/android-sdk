package io.blockv.face.client

import io.blockv.core.model.Action
import io.blockv.core.model.Face
import io.blockv.core.model.Vatom

interface FaceViewFactory {

  val displayUrl: String

  fun emitFaceView(vatom: Vatom, face: Face, actions: List<Action>, bridge: FaceBridge?): FaceView

}