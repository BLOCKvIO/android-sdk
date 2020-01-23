package io.blockv.common.model

import io.blockv.common.internal.json.serializer.Serializer

class UserConsent @Serializer.Serializable constructor(
  @Serializer.Serialize(name = "app_id")
  val appId: String,
  @Serializer.Serialize
  val version: Int,
  @Serializer.Serialize
  val address: String,
  @Serializer.Serialize
  val meta: Meta
) : Model {

  private val hash = toString().hashCode()

  class Meta @Serializer.Serializable constructor(
    @Serializer.Serialize(name = "created_by")
    val createdBy: String,
    @Serializer.Serialize(name = "modified_by")
    val modifiedBy: String,
    @Serializer.Serialize(name = "when_created")
    val whenCreated: String,
    @Serializer.Serialize(name = "when_modified")
    val whenModified: String
  ) {
    override fun toString(): String {
      return "Meta{" +
        " createdBy='" + createdBy + '\'' +
        ", modifiedBy='" + modifiedBy + '\'' +
        ", whenCreated='" + whenCreated + '\'' +
        ", whenModified='" + whenModified + '\'' +
        "}"
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is UserConsent) return false
    return hashCode() == other.hashCode()
  }

  override fun hashCode(): Int {
    return hash
  }

  override fun toString(): String {
    return "UserConsent{" +
      " appId='" + appId + '\'' +
      ", version='" + version + '\'' +
      ", address='" + address + '\'' +
      ", meta='" + meta + '\'' +
      "}"
  }
}