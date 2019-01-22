package io.blockv.common.model

import io.blockv.common.internal.json.serializer.Serializer

class Pack : Model {

  @Serializer.Serialize
  val vatoms: List<Vatom>
  @Serializer.Serialize
  val faces: List<Face>
  @Serializer.Serialize
  val actions: List<Action>

  @Serializer.Serializable
  constructor(
    vatoms: List<Vatom>,
    faces: List<Face>,
    actions: List<Action>
  ) {
    this.vatoms = vatoms
    this.faces = faces
    this.actions = actions
  }
}