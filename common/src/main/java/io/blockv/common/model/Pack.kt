package io.blockv.common.model

import io.blockv.common.internal.json.serializer.Serializer

class Pack @Serializer.Serializable constructor(
  @Serializer.Serialize
  val vatoms: List<Vatom>,
  @Serializer.Serialize
  val faces: List<Face>,
  @Serializer.Serialize
  val actions: List<Action>
) : Model