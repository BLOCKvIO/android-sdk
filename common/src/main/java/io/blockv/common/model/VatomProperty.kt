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

class VatomProperty() {

  var commerce: Commerce? = null

  var isAcquireable: Boolean = false

  var author: String? = null

  var category: String? = null

  var clonedFrom: String? = null

  var cloningScore: Float? = 0f

  var description: String? = null

  var isDropped: Boolean = false

  var geoPos: GeoPosition? = null

  var isInContract: Boolean = false

  var inContractWith: String? = null

  var isInReaction: Boolean = false

  var notifyMsg: String? = null

  var numDirectClones: Int? = 0

  var owner: String? = null

  var parentId: String? = null

  var publisherFqdn: String? = null

  var reactedBy: String? = null

  var reactionExpires: String? = null

  var rootType: String? = null

  var tags: List<String>? = null

  var templateId: String = ""

  var templateVariationId: String = ""

  var title: String? = null

  var isTransferable: Boolean = false

  var transferredBy: String? = null

  var isRedeemable: Boolean = false

  var resources: List<Resource> = ArrayList()
    set(value) {
      field = value
      _resources.clear()
      field.forEach { _resources.put(it.name, it) }
    }

  private val _resources: HashMap<String, Resource> = HashMap<String, Resource>()

  var childPolicy: List<ChildPolicy>? = null

  var visibility: VatomVisibility? = null

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

