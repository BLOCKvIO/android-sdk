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
package io.blockv.common.model

import io.blockv.common.internal.json.serializer.Serializer

class InventoryEvent @Serializer.Serializable constructor(
  @Serializer.Serialize(name = "event_id")
  val eventId: String,
  @Serializer.Serialize(name = "op")
  val operation: String,
  @Serializer.Serialize(name = "id")
  val vatomId: String,
  @Serializer.Serialize(name = "new_owner")
  val newOwnerId: String,
  @Serializer.Serialize(name = "old_owner")
  val oldOwnerId: String,
  @Serializer.Serialize(name = "template_variation")
  val templateVariationId: String,
  @Serializer.Serialize(name = "parent_id")
  val parentId: String,
  @Serializer.Serialize(name = "action_name")
  val actionName: String
) : Model {

  override fun toString(): String {
    return "InventoryEvent{" +
      "eventId='" + eventId + '\'' +
      ", operation='" + operation + '\'' +
      ", vatomId='" + vatomId + '\'' +
      ", newOwnerId='" + newOwnerId + '\'' +
      ", oldOwnerId='" + oldOwnerId + '\'' +
      ", templateVariationId='" + templateVariationId + '\'' +
      ", parentId='" + parentId + '\'' +
      ", actionName='" + actionName + '\'' +
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