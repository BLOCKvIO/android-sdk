/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.internal.json.deserializer.event

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.event.InventoryEvent
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