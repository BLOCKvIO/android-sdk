package io.blockv.core.internal.repository.model

import androidx.room.Embedded
import androidx.room.Relation
import io.blockv.common.model.Action
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom

class VatomPack {
  @Embedded
  var vatom: Vatom? = null

  @Relation(parentColumn = "templateId", entityColumn = "templateId")
  var faces: List<Face>? = null

  @Relation(parentColumn = "templateId", entityColumn = "templateId")
  var actions: List<Action>? = null

  var count: Int = 1
}