package io.blockv.common.model

import io.blockv.common.internal.json.serializer.Serializer

class VatomUpdate : Model {
  @Serializer.Serialize
  val ids: List<String>
  @Serializer.Serialize(name = "num_updated")
  val numberUpdated: Int
  @Serializer.Serialize(name = "num_errors")
  val numberErrors: Int
  @Serializer.Serialize(name = "error_messages")
  val errorMessages: Map<String, String>

  @Serializer.Serializable
  constructor(
    ids: List<String>,
    numberUpdated: Int,
    numberErrors: Int,
    errorMessages: Map<String, String>
  ) {
    this.ids = ids
    this.numberUpdated = numberUpdated
    this.numberErrors = numberErrors
    this.errorMessages = errorMessages
  }
}