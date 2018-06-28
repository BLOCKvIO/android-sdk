package io.blockv.core.internal.json.deserializer.event

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.InventoryEvent
import org.json.JSONObject

class InventoryEventDeserializer : Deserializer<InventoryEvent> {
  override fun deserialize(data: JSONObject): InventoryEvent? {
    try {
      val eventId = data.getString("event_id")
      val operation = data.getString("op")
      val vatomId = data.getString("id")
      val newOwner = data.getString("new_owner")
      val oldOwner = data.getString("old_owner")
      val templateVariationId = data.getString("template_variation")
      val parentId = data.getString("parent_id")
      return InventoryEvent(
        eventId,
        operation,
        vatomId,
        newOwner,
        oldOwner,
        templateVariationId,
        parentId)
    } catch (e: Exception) {
      android.util.Log.e("InvEventDeserializer", e.message)
    }
    return null
  }
}