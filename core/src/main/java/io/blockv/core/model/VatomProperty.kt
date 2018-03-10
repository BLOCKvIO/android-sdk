package io.blockv.core.model

class VatomProperty {

  var commerce:Commerce? = null

  var acquireable: Boolean? = false

  var author: String? = null

  var category: String? = null

  var clonedFrom: String? = null

  var cloningScore: Float? = 0f

  var description: String? = null

  var dropped: Boolean? = false

  var geoPos: GeoPosition? = null

  var inContract: Boolean? = false

  var inContractWith: String? = null

  var inReaction: Boolean? = false

  var notifyMsg: String? = null

  var numDirectClones: Int? = 0

  var owner: String? = null

  var parentId: String? = null

  var publisherFqdn: String? = null

  var reactedBy: String? = null

  var reactionExpires: String? = null

  var rootType: String? = null

  var tags: List<String>? = null

  var template: String? = null

  var templateVariation: String? = null

  var title: String? = null

  var transferable: Boolean? = false

  var transferedBy: String? = null

  var resources: List<Resource>? = null

  var childPolicy: List<ChildPolicy>? = null

  var visibility: VatomVisibility? = null

  var tradeable:Boolean? = null

  override fun toString(): String {
    return "VatomProperty{" +
      commerce +
      ", acquireable=" + acquireable +
      ", author='" + author + '\'' +
      ", category='" + category + '\'' +
      ", clonedFrom='" + clonedFrom + '\'' +
      ", cloningScore=" + cloningScore +
      ", description='" + description + '\'' +
      ", dropped=" + dropped +
      ", geoPos=" + geoPos +
      ", inContract=" + inContract +
      ", inContractWith='" + inContractWith + '\'' +
      ", inReaction=" + inReaction +
      ", notifyMsg='" + notifyMsg + '\'' +
      ", numDirectClones=" + numDirectClones +
      ", owner='" + owner + '\'' +
      ", parentId='" + parentId + '\'' +
      ", publisherFqdn='" + publisherFqdn + '\'' +
      ", reactedBy='" + reactedBy + '\'' +
      ", reactionExpires='" + reactionExpires + '\'' +
      ", rootType='" + rootType + '\'' +
      ", tags=" + tags +
      ", template='" + template + '\'' +
      ", templateVariation='" + templateVariation + '\'' +
      ", title='" + title + '\'' +
      ", transferable=" + transferable +
      ", transferedBy='" + transferedBy + '\'' +
      ", resources=" + resources +
      ", childPolicy=" + childPolicy +
      ", visibility=" + visibility +
      '}'
  }
}

