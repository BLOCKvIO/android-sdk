package io.blockv.core.model

class InventoryEvent(val eventId: String,
                     val operation: String,
                     val vatomId: String,
                     val newOwnerId: String,
                     val oldOwnerId: String,
                     val templateVariationId: String,
                     val parentId: String)