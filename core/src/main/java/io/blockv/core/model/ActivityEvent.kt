package io.blockv.core.model

class ActivityEvent(val eventId: Long,
                    val targetUserId: String,
                    val triggerUserId: String,
                    val vatomIds: List<String>,
                    val resources: List<Resource>,
                    val message: String,
                    val actionName: String,
                    val whenCreated: String){

  override fun toString(): String {
    return "ActivityEvent{" +
      "eventId='" + eventId + '\'' +
      ", targetUserId='" + targetUserId + '\'' +
      ", triggerUserId='" + triggerUserId + '\'' +
      ", vatomIds='" + vatomIds + '\'' +
      ", resources='" + resources + '\'' +
      ", message='" + message + '\'' +
      ", actionName='" + actionName + '\'' +
      ", whenCreated='" + whenCreated + '\'' +
      "}"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ActivityEvent) return false
    return eventId == other.eventId
  }

  override fun hashCode(): Int {
    return eventId.hashCode()
  }
}
