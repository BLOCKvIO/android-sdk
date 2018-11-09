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

class ActivityMessage {

  @Serializer.Serialize(name = "msg_id")
  val id: Long
  @Serializer.Serialize(name = "user_id")
  val targetUserId: String
  @Serializer.Serialize(name = "vatoms")
  val vatomIds: List<String>
  @Serializer.Serialize(name = "templ_vars")
  val templateVariationIds: List<String>
  @Serializer.Serialize(name = "msg")
  val message: String
  @Serializer.Serialize(name = "action_name")
  val actionName: String
  @Serializer.Serialize(name = "when_created")
  val whenCreated: String
  @Serializer.Serialize(name = "triggered_by")
  val triggerUserId: String
  @Serializer.Serialize(name = "generic")
  val resources: List<Resource>
  @Serializer.Serialize(name = "geo_pos")
  val geoPosition: List<Double>

  @Serializer.Serializable
  constructor(
    id: Long,
    targetUserId: String,
    vatomIds: List<String>,
    templateVariationIds: List<String>,
    message: String,
    actionName: String,
    whenCreated: String,
    triggerUserId: String,
    resources: List<Resource>,
    geoPosition: List<Double>
  ) {
    this.id = id
    this.targetUserId = targetUserId
    this.vatomIds = vatomIds
    this.templateVariationIds = templateVariationIds
    this.message = message
    this.actionName = actionName
    this.whenCreated = whenCreated
    this.triggerUserId = triggerUserId
    this.resources = resources
    this.geoPosition = geoPosition
  }

  private val hash = toString().hashCode()

  override fun toString(): String {
    return "ActivityMessage{" +
      "id='" + id + '\'' +
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
    if (other !is ActivityMessage) return false
    return hashCode() == other.hashCode()
  }

  override fun hashCode(): Int {
    return hash
  }
}