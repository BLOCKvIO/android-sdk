package io.blockv.common.model

import io.blockv.common.internal.json.serializer.Serializer

class InventorySync @Serializer.Serializable constructor(
  @Serializer.Serialize
  val vatoms: List<VatomSync>,
  @Serializer.Serialize(name = "next_token")
  val token: String
) : Model {
  class VatomSync @Serializer.Serializable constructor(
    @Serializer.Serialize
    val id: String,
    @Serializer.Serialize
    val sync: Int
  )
}