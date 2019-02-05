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

class ActivityEvent : Model {

  @Serializer.Serialize(name = "msg_id")
  val eventId: Long
  @Serializer.Serialize(name = "user_id")
  val targetUserId: String
  @Serializer.Serialize(name = "triggered_by")
  val triggerUserId: String
  @Serializer.Serialize(name = "vatoms")
  val vatomIds: List<String>
  @Serializer.Serialize(name = "generic")
  val resources: List<Resource>
  @Serializer.Serialize(name = "msg")
  val message: String
  @Serializer.Serialize(name = "action_name")
  val actionName: String
  @Serializer.Serialize(name = "when_created")
  val whenCreated: String

  @Serializer.Serializable
  constructor(
    eventId: Long,
    targetUserId: String,
    triggerUserId: String,
    vatomIds: List<String>,
    resources: List<Resource>,
    message: String,
    actionName: String,
    whenCreated: String
  ) {
    this.eventId = eventId
    this.targetUserId = targetUserId
    this.triggerUserId = triggerUserId
    this.vatomIds = vatomIds
    this.resources = resources
    this.message = message
    this.actionName = actionName
    this.whenCreated = whenCreated
  }


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
