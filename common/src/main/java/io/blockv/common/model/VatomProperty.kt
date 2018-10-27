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

class VatomProperty() {

  @JsonModule.Serialize
  var commerce: Commerce? = null

  @JsonModule.Serialize(name = "acquirable")
  var isAcquireable: Boolean = false

  @JsonModule.Serialize
  var author: String? = null

  @JsonModule.Serialize
  var category: String? = null

  @JsonModule.Serialize(name = "cloned_from")
  var clonedFrom: String? = null

  @JsonModule.Serialize(name = "cloning_score")
  var cloningScore: Float? = 0f

  @JsonModule.Serialize
  var description: String? = null

  @JsonModule.Serialize(name = "dropped")
  var isDropped: Boolean = false

  @JsonModule.Serialize(name = "geo_pos")
  var geoPos: GeoPosition? = null

  @JsonModule.Serialize(name = "in_contract")
  var isInContract: Boolean = false

  @JsonModule.Serialize(name = "in_contract_with")
  var inContractWith: String? = null

  @JsonModule.Serialize(name = "in_reaction")
  var isInReaction: Boolean = false

  @JsonModule.Serialize(name = "notify_msg")
  var notifyMsg: String? = null

  @JsonModule.Serialize(name = "num_direct_clones")
  var numDirectClones: Int? = 0

  @JsonModule.Serialize
  var owner: String? = null

  @JsonModule.Serialize(name = "parent_id")
  var parentId: String? = null

  @JsonModule.Serialize(name = "publisher_fqdn")
  var publisherFqdn: String? = null

  @JsonModule.Serialize(name = "reacted_by")
  var reactedBy: String? = null

  @JsonModule.Serialize(name = "reaction_expires")
  var reactionExpires: String? = null

  @JsonModule.Serialize(name = "root_type")
  var rootType: String? = null

  @JsonModule.Serialize
  var tags: List<String>? = null

  @JsonModule.Serialize(name = "template")
  var templateId: String = ""

  @JsonModule.Serialize(name = "template_variation")
  var templateVariationId: String = ""

  @JsonModule.Serialize
  var title: String? = null

  @JsonModule.Serialize(name = "transferable")
  var isTransferable: Boolean = false

  @JsonModule.Serialize(name = "transferred_by")
  var transferredBy: String? = null

  @JsonModule.Serialize(name = "redeemable")
  var isRedeemable: Boolean = false

  @JsonModule.Serialize
  var resources: List<Resource> = ArrayList()
    set(value) {
      field = value
      _resources.clear()
      field.forEach { _resources.put(it.name, it) }
    }

  private val _resources: HashMap<String, Resource> = HashMap<String, Resource>()

  @JsonModule.Serialize(name = "child_policy")
  var childPolicy: List<ChildPolicy>? = null

  @JsonModule.Serialize
  var visibility: VatomVisibility? = null

  @JsonModule.Serialize(name = "tradeable")
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
    this.isInReaction = property.isInReaction
    this.notifyMsg = property.notifyMsg
    this.numDirectClones = property.numDirectClones
    this.owner = property.owner
    this.parentId = property.parentId
    this.publisherFqdn = property.publisherFqdn
    this.reactedBy = property.reactedBy
    this.reactionExpires = property.reactionExpires
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
      ", inReaction=" + isInReaction +
      ", notifyMsg='" + notifyMsg + '\'' +
      ", numDirectClones=" + numDirectClones +
      ", owner='" + owner + '\'' +
      ", parentId='" + parentId + '\'' +
      ", publisherFqdn='" + publisherFqdn + '\'' +
      ", reactedBy='" + reactedBy + '\'' +
      ", reactionExpires='" + reactionExpires + '\'' +
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

