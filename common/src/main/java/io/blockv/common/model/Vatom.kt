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

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import io.blockv.common.internal.json.serializer.Serializer
import org.json.JSONObject

@Entity
open class Vatom @Serializer.Serializable constructor(
  @PrimaryKey
  @Serializer.Serialize
  val id: String,
  @Serializer.Serialize(name = "when_created")
  val whenCreated: String,
  @Serializer.Serialize(name = "when_modified")
  var whenModified: String,
  @Serializer.Serialize(name = "when_added")
  var whenAdded: String,
  @Embedded
  @Serializer.Serialize(name = "vAtom::vAtomType")
  val property: VatomProperty,
  @Serializer.Serialize
  val private: JSONObject?,
  @Serializer.Serialize
  val sync: Int,
  @Ignore
  @Serializer.Serialize
  var faces: List<Face>,
  @Ignore
  @Serializer.Serialize
  var actions: List<Action>
) : Model {

  constructor(
    id: String,
    whenCreated: String,
    whenModified: String,
    whenAdded: String,
    property: VatomProperty,
    private: JSONObject?,
    sync: Int
  ) : this(id, whenCreated, whenModified, whenAdded, property, private,sync, emptyList(), emptyList())

  @Ignore
  val rootType: Type

  val isContainer: Boolean
  get() {
    return rootType != Type.STANDARD || rootType != Type.UNKNOWN
  }

  init {
    this.rootType = Type.from(property.rootType ?: "")
  }

  override fun toString(): String {
    return "Vatom{" +
      "id='" + id + '\'' +
      ",whenCreated='" + whenCreated + '\'' +
      ", whenModified='" + whenModified + '\'' +
      ", whenAdded='" + whenAdded + '\'' +
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

  enum class Type {
    STANDARD,
    CONTAINER_FOLDER,
    CONTAINER_DISCOVER,
    CONTAINER_PACKAGE,
    CONTAINER_DEFINED,
    UNKNOWN;

    companion object {
      fun from(type: String): Type {
        return when (type) {
          "vAtom::vAtomType" -> STANDARD
          "vAtom::vAtomType::DefinedFolderContainerType" -> CONTAINER_DEFINED
          "vAtom::vAtomType::FolderContainerType" -> CONTAINER_FOLDER
          "vAtom::vAtomType::DiscoverContainerType" -> CONTAINER_DISCOVER
          "vAtom::vAtomType::PackageContainerType" -> CONTAINER_PACKAGE
          else -> UNKNOWN
        }
      }
    }
  }
}
