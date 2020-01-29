package io.blockv.common.model

import io.blockv.common.internal.json.serializer.Serializer

class Account @Serializer.Serializable constructor(
  @Serializer.Serialize
  val id: String,
  @Serializer.Serialize(name = "user_id")
  val userId: String,
  @Serializer.Serialize
  val address: String,
  @Serializer.Serialize
  val type: String,
  @Serializer.Serialize(name = "created_at")
  val createdAt: String
) : Model {

  override fun toString(): String {
    return "Account{" +
      " id='" + id + '\'' +
      " userId='" + userId + '\'' +
      " address='" + address + '\'' +
      " type='" + type + '\'' +
      " createdAt='" + createdAt + '\'' +
      "}"
  }
}