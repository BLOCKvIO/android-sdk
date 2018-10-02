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

class ActivityEvent(
  val eventId: Long,
  val targetUserId: String,
  val triggerUserId: String,
  val vatomIds: List<String>,
  val resources: List<Resource>,
  val message: String,
  val actionName: String,
  val whenCreated: String
) {

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
