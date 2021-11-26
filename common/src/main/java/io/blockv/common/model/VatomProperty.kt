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
import androidx.room.Ignore
import io.blockv.common.internal.json.serializer.Serializer

class VatomProperty() : Model {

  @Embedded
  @Serializer.Serialize
  var commerce: Commerce? = null

  @Serializer.Serialize(name = "activate_action")
  var activateAction: String? = null

  @Serializer.Serialize(name = "acquirable")
  var isAcquireable: Boolean = false

  @Serializer.Serialize
  var author: String? = null

  @Serializer.Serialize
  var category: String? = null

  @Serializer.Serialize(name = "cloned_from")
  var clonedFrom: String? = null

  @Serializer.Serialize(name = "cloning_score")
  var cloningScore: Float? = 0f

  @Serializer.Serialize
  var description: String? = null

  @Serializer.Serialize(name = "dropped")
  var isDropped: Boolean = false

  @Embedded(prefix = "geo")
  @Serializer.Serialize(name = "geo_pos")
  var geoPos: GeoPosition? = null

  @Serializer.Serialize(name = "in_contract")
  var isInContract: Boolean = false

  @Serializer.Serialize(name = "in_contract_with")
  var inContractWith: String? = null

  @Serializer.Serialize(name = "notify_msg")
  var notifyMsg: String? = null

  @Serializer.Serialize(name = "num_direct_clones")
  var numDirectClones: Int? = 0

  @Serializer.Serialize
  var owner: String? = null

  @Serializer.Serialize(name = "parent_id")
  var parentId: String? = null

  @Serializer.Serialize(name = "publisher_fqdn")
  var publisherFqdn: String? = null

  @Serializer.Serialize(name = "root_type")
  var rootType: String? = null

  @Serializer.Serialize
  var tags: List<String>? = null

  @Serializer.Serialize(name = "template")
  var templateId: String = ""

  @Serializer.Serialize(name = "template_variation")
  var templateVariationId: String = ""

  @Serializer.Serialize
  var title: String? = null

  @Serializer.Serialize(name = "transferable")
  var isTransferable: Boolean = false

  @Serializer.Serialize(name = "transferred_by")
  var transferredBy: String? = null

  @Serializer.Serialize(name = "redeemable")
  var isRedeemable: Boolean = false

  @Serializer.Serialize
  var resources: List<Resource> = ArrayList()
    set(value) {
      field = value
      _resources.clear()
      field.forEach { _resources.put(it.name, it) }
    }

  @Ignore
  private val _resources: HashMap<String, Resource> = HashMap<String, Resource>()

  @Serializer.Serialize(name = "child_policy", default = false)
  var childPolicy: List<ChildPolicy>? = null

  @Embedded(prefix = "visibility")
  @Serializer.Serialize
  var visibility: VatomVisibility? = null

  @Serializer.Serialize(name = "tradeable")
  var isTradeable: Boolean = false

  constructor(property: VatomProperty) : this() {
    this.isAcquireable = property.isAcquireable
    this.author = property.author
    this.commerce = if (property.commerce != null) Commerce(property.commerce!!) else null
    this.category = property.category
    this.clonedFrom = property.clonedFrom
    this.cloningScore = property.cloningScore
    this.description = property.description
    this.isDropped = property.isDropped
    this.geoPos = if (property.geoPos != null) GeoPosition(property.geoPos!!) else null
    this.isInContract = property.isInContract
    this.inContractWith = property.inContractWith
    this.notifyMsg = property.notifyMsg
    this.numDirectClones = property.numDirectClones
    this.owner = property.owner
    this.parentId = property.parentId
    this.publisherFqdn = property.publisherFqdn
    this.rootType = property.rootType
    this.tags = property.tags
    this.templateId = property.templateId
    this.templateVariationId = property.templateVariationId
    this.title = property.title
    this.isTransferable = property.isTransferable
    this.transferredBy = property.transferredBy
    this.isRedeemable = property.isRedeemable
    this.resources = ArrayList(property.resources)
    this.childPolicy = if (property.childPolicy == null) null else ArrayList(property.childPolicy)
    this.isTradeable = property.isTradeable
    this.visibility = if (property.visibility != null) VatomVisibility(property.visibility!!) else null
  }

  fun getResource(name: String): Resource? {
    val resources = _resources
    return resources[name]
  }

  override fun toString(): String {
    return "VatomProperty{" +
      commerce +
      ", acquireable=" + isAcquireable +
      ", author='" + author + '\'' +
      ", category='" + category + '\'' +
      ", clonedFrom='" + clonedFrom + '\'' +
      ", cloningScore=" + cloningScore +
      ", description='" + description + '\'' +
      ", dropped=" + isDropped +
      ", geoPos=" + geoPos +
      ", inContract=" + isInContract +
      ", inContractWith='" + inContractWith + '\'' +
      ", notifyMsg='" + notifyMsg + '\'' +
      ", numDirectClones=" + numDirectClones +
      ", owner='" + owner + '\'' +
      ", parentId='" + parentId + '\'' +
      ", publisherFqdn='" + publisherFqdn + '\'' +
      ", rootType='" + rootType + '\'' +
      ", tags=" + tags +
      ", templateId='" + templateId + '\'' +
      ", templateVariationId='" + templateVariationId + '\'' +
      ", title='" + title + '\'' +
      ", transferable=" + isTransferable +
      ", transferredBy='" + transferredBy + '\'' +
      ", resources=" + resources +
      ", childPolicy=" + childPolicy +
      ", visibility=" + visibility +
      '}'
  }


}

