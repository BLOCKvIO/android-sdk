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
package io.blockv.core.internal.json.deserializer.vatom

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.*
import org.json.JSONArray
import org.json.JSONObject

class VatomDeserializer(val faceDeserializer: Deserializer<Face?>,
                        val actionDeserializer: Deserializer<Action?>) : Deserializer<Vatom> {
  override fun deserialize(data: JSONObject): Vatom? {

    try {
      val prop: JSONObject = data.getJSONObject("vAtom::vAtomType")
      val id: String = data.getString("id")
      val private: JSONObject? = data.optJSONObject("private")
      val whenCreated: String = data.getString("when_created")
      val whenModified: String = data.getString("when_modified")
      val properties = VatomProperty()
      properties.author = prop.optString("author")
      properties.category = prop.optString("category")
      properties.clonedFrom = prop.optString("cloned_from")
      properties.isAcquireable = prop.optBoolean("acquireable")
      properties.cloningScore = prop.optDouble("cloning_score").toFloat()
      properties.description = prop.optString("description")
      properties.isDropped = prop.optBoolean("dropped")
      properties.isInContract = prop.optBoolean("in_contract")
      properties.inContractWith = prop.optString("in_contract_with")
      properties.isInReaction = prop.optBoolean("in_reaction")
      properties.notifyMsg = prop.optString("notify_msg")
      properties.numDirectClones = prop.optInt("num_direct_clones")
      properties.owner = prop.getString("owner")
      properties.parentId = prop.optString("parent_id", ".")
      properties.publisherFqdn = prop.optString("publisher_fqdn")
      properties.reactedBy = prop.optString("reacted_by")
      properties.reactionExpires = prop.optString("reaction_expires")
      properties.rootType = prop.optString("root_type")
      properties.templateId = prop.getString("template")
      properties.templateVariationId = prop.getString("template_variation")
      properties.title = prop.optString("title")
      properties.isTradeable = prop.optBoolean("tradeable")
      properties.isTransferable = prop.optBoolean("transferable")
      properties.transferredBy = prop.optString("transferred_by")
      properties.isRedeemable = prop.optBoolean("redeemable")

      if (prop.has("child_policy") && !prop.isNull("child_policy")) {
        val policyArray = prop.optJSONArray("child_policy")
        val childPolicy: ArrayList<ChildPolicy> = ArrayList()
        (0 until policyArray.length())
          .mapTo(childPolicy) {
            val policy = policyArray.optJSONObject(it)
            val creation = policy.optJSONObject("creation_policy") ?: JSONObject()
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
      }
      val visibility: JSONObject = prop.optJSONObject("visibility")
      properties.visibility = VatomVisibility(visibility.optString("type"), visibility.optString("value", "*"))

      val tagArray: org.json.JSONArray? = prop.optJSONArray("tags")
      if (tagArray != null) {
        val tags: ArrayList<String> = ArrayList(tagArray.length())
        (0 until tagArray.length()).mapTo(tags) { tagArray.optString(it) }
        properties.tags = tags
      } else
        properties.tags = ArrayList()


      val geoPos = prop.optJSONObject("geo_pos")
      val coordArray: org.json.JSONArray = geoPos.optJSONArray("coordinates")
      val coordinates: ArrayList<Float> = ArrayList(coordArray.length())
      (0 until coordArray.length()).mapTo(coordinates) { coordArray.optDouble(it).toFloat() }

      properties.geoPos = GeoPosition(
        geoPos.optString("type", "Point"),
        geoPos.optString("\$reql_type\$", "GEOMETRY"),
        coordinates
      )

      if (prop.has("commerce")) {
        val commerce: JSONObject = prop.optJSONObject("commerce")
        val pricing = commerce.optJSONObject("pricing")
        val value = pricing.optJSONObject("value")

        properties.commerce = Commerce(
          Pricing(
            pricing.optString("pricingType", "Fixed"),
            value.optString("currency"),
            value.optString("price"),
            value.optString("valid_from", "*"),
            value.optString("valid_through", "*"),
            value.optBoolean("vat_included", false)
          )
        )
      }
      val resourceArray = prop.optJSONArray("resources") ?: JSONArray()
      val resources: ArrayList<Resource> = ArrayList()
      (0 until resourceArray.length())
        .forEach {
          val resource = resourceArray.optJSONObject(it)
          if (resource != null) {
            resources.add(
              Resource(
                resource.optString("name"),
                resource.optString("resourceType"),
                resource.optJSONObject("value").optString("value")
              )
            )
          }
        }
      properties.resources = resources

      val faces: JSONArray? = data.optJSONArray("faces")
      val actions: JSONArray? = data.optJSONArray("actions")
      val facesArray: ArrayList<Face> = ArrayList()
      val actionsArray: ArrayList<Action> = ArrayList()

      if (faces != null) {
        (0 until faces.length())
          .forEach {
            val face: Face? = faceDeserializer.deserialize(faces.optJSONObject(it))
            if (face != null) {
              facesArray.add(face)
            }
          }
      }
      if (actions != null) {
        (0 until actions.length())
          .forEach {
            val action: Action? = actionDeserializer.deserialize(actions.optJSONObject(it))
            if (action != null) {
              actionsArray.add(action)
            }
          }
      }

      return Vatom(
        id,
        whenCreated,
        whenModified,
        properties,
        private,
        facesArray,
        actionsArray
      )
    } catch (e: Exception) {
      android.util.Log.e("VatomDeserializer", e.message)
    }
    return null
  }

}