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
package io.blockv.core.model

import org.json.JSONObject

open class Vatom(
  val id: String,
  val whenCreated: String,
  val whenModified: String,
  val property: VatomProperty,
  val private: JSONObject?,
  val faces: List<Face>,
  val actions: List<Action>
) {

  override fun toString(): String {
    return "Vatom{" +
      "id='" + id + '\'' +
      ",whenCreated='" + whenCreated + '\'' +
      ", whenModified='" + whenModified + '\'' +
      "," + property + '\'' +
      ", private='" + private + '\'' +
      "}"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Vatom) return false
    return id == other.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }
}
