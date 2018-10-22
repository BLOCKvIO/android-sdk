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

import io.blockv.common.internal.json.JsonModule
import org.json.JSONObject

open class Vatom {

  @JsonModule.Serialize
  val id: String
  @JsonModule.Serialize(name = "when_created")
  val whenCreated: String
  @JsonModule.Serialize(name = "when_modified")
  val whenModified: String
  @JsonModule.Serialize(name = "vAtom::vAtomType")
  val property: VatomProperty
  @JsonModule.Serialize
  val private: JSONObject?
  @JsonModule.Serialize
  val faces: List<Face>
  @JsonModule.Serialize
  val actions: List<Action>

  @JsonModule.Serializable
  constructor(
    id: String,
    whenCreated: String,
    whenModified: String,
    property: VatomProperty,
    private: JSONObject?,
    faces: List<Face>,
    actions: List<Action>
  ) {
    this.id = id
    this.whenCreated = whenCreated
    this.whenModified = whenModified
    this.property = property
    this.private = private
    this.faces = faces
    this.actions = actions
  }

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
