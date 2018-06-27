package io.blockv.core.model

class InventoryEvent(val eventId: String,
                     val operation: String,
                     val vatomId: String,
                     val newOwnerId: String,
                     val oldOwnerId: String,
                     val templateVariationId: String,
                     val parentId: String) {

  override fun toString(): String {
    return "InventoryEvent{" +
      "eventId='" + eventId + '\'' +
      ", operation='" + operation + '\'' +
      ", vatomId='" + vatomId + '\'' +
      ", newOwnerId='" + newOwnerId + '\'' +
      ", oldOwnerId='" + oldOwnerId + '\'' +
      ", templateVariationId='" + templateVariationId + '\'' +
      ", parentId='" + parentId + '\'' +
      "}"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is InventoryEvent) return false
    return eventId == other.eventId
  }

  override fun hashCode(): Int {
    return eventId.hashCode()
  }
}