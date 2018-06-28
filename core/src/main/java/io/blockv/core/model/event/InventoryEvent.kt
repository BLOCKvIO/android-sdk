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
package io.blockv.core.model.event

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