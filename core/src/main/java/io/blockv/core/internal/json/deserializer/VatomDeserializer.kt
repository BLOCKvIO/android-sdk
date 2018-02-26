package io.blockv.core.internal.json.deserializer

import io.blockv.android.core.model.*
import io.blockv.core.model.*
import org.json.JSONObject

class VatomDeserializer : Deserializer<Vatom> {
  override fun deserialize(data: JSONObject): Vatom? {

    try {
      val prop: JSONObject = data.getJSONObject("vAtomic::v1::vAtom::vAtomType")
      val id: String? = data.getString("id")
      val private: JSONObject? = data.optJSONObject("private")
      val whenCreated: String? = data.getString("when_created")
      val whenModified: String? = data.getString("when_modified")
      val properties: VatomProperty = VatomProperty()
      properties.author = prop.optString("author")
      properties.category = prop.optString("category")
      properties.clonedFrom = prop.optString("cloned_from")
      properties.acquireable = prop.optBoolean("acquireable")
      properties.cloningScore = prop.optDouble("cloning_score").toFloat()
      properties.description = prop.optString("description")
      properties.dropped = prop.optBoolean("dropped")
      properties.inContract = prop.optBoolean("in_contract")
      properties.inContractWith = prop.optString("in_contract_with")
      properties.inReaction = prop.optBoolean("in_reaction")
      properties.notifyMsg = prop.optString("notify_msg")
      properties.numDirectClones = prop.optInt("num_direct_clones")
      properties.owner = prop.getString("owner")
      properties.parentId = prop.optString("parent_id", ".")
      properties.publisherFqdn = prop.optString("publisher_fqdn")
      properties.reactedBy = prop.optString("reacted_by")
      properties.reactionExpires = prop.optString("reaction_expires")
      properties.rootType = prop.optString("root_type")
      properties.template = prop.getString("template")
      properties.templateVariation = prop.getString("template_variation")
      properties.title = prop.optString("title")
      properties.tradeable = prop.optBoolean("tradeable")
      properties.transferable = prop.optBoolean("transferable")
      properties.transferedBy = prop.optString("transfered_by")

      val policyArray = prop.optJSONArray("child_policy")
      val childPolicy: ArrayList<ChildPolicy> = ArrayList()
      (0..policyArray.length())
        .mapTo(childPolicy) {
          val policy =policyArray.optJSONObject(it)
          val creation = policy.optJSONObject("creation_policy")
          ChildPolicy(
            policy.optInt("count"),
            policy.optString("template_variation"),
            CreationPolicy(
              creation.optString("auto_create"),
              creation.optInt("auto_create_count"),
              creation.optBoolean("auto_create_count_random"),
              creation.optBoolean("enforce_policy_count_max"),
              creation.optBoolean("enforce_policy_count_min"),
              creation.optInt("policy_count_max"),
              creation.optInt("policy_count_min")
            )
          )
        }
        .filter { android.text.TextUtils.isEmpty(it.templateVariation) }

      properties.childPolicy = childPolicy

      val visibility: JSONObject = prop.optJSONObject("visibility")
      properties.visibility = VatomVisibility(visibility.optString("type"), visibility.optString("value", "*"))

      val tagArray: org.json.JSONArray = prop.optJSONArray("tags")
      val tags: ArrayList<String> = ArrayList(tagArray.length())
      (0..tagArray.length()).mapTo(tags) { tagArray.optString(it) }
      properties.tags = tags

      val geoPos = prop.optJSONObject("geo_pos")
      val coordArray: org.json.JSONArray = geoPos.optJSONArray("coordinates")
      val coordinates: ArrayList<Float> = ArrayList(coordArray.length())
      (0..coordArray.length()).mapTo(coordinates) { coordArray.optDouble(it).toFloat() }

      properties.geoPos = GeoPosition(
        geoPos.optString("type", "Point"),
        geoPos.optString("\$reql_type\$", "GEOMETRY"),
        coordinates)

      val commerce: JSONObject = prop.optJSONObject("MyCommerce")
      val pricing = commerce.optJSONObject("pricing")
      val value = pricing.optJSONObject("value")

      properties.commerce = Commerce(
        commerce.optBoolean("redeemable"),
        Pricing(
          pricing.optString("v1::PricingType", "Fixed"),
          value.optString("currency"),
          value.optString("price"),
          value.optString("valid_from", "*"),
          value.optString("valid_through", "*"),
          value.optBoolean("valid_through", false)))

      val resourceArray = prop.optJSONArray("resources")
      val resources: ArrayList<VatomResource> = ArrayList(resourceArray.length())
      (0..resourceArray.length())
        .map { resourceArray.optJSONObject(it) }
        .mapTo(resources) {
          VatomResource(
            it.optString("name")
            , it.optString("v1::ResourceType"),
            it.optJSONObject("value").optString("value")
          )
        }

      return Vatom(
        id,
        whenCreated,
        whenModified,
        properties,
        private)
    } catch (e: Exception) {
      android.util.Log.w("VatomDeserializer", e.message)
    }
    return null
  }

}