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
package io.blockv.core.model.activity

import io.blockv.core.model.vatom.Resource

class ActivityMessage(val id: Long,
                      val targetUserId: String,
                      val vatomIds: List<String>,
                      val templateVariationIds: List<String>,
                      val message: String,
                      val actionName: String,
                      val whenCreated: String,
                      val triggerUserId: String,
                      val resources: List<Resource>,
                      val geoPosition: List<Double>) {

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