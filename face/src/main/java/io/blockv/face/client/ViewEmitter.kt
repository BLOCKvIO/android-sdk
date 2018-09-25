package io.blockv.face.client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.blockv.common.model.Vatom

interface ViewEmitter {

  fun emit(inflater: LayoutInflater, parent: ViewGroup, vatom: Vatom, resourceManager: ResourceManager): View
}