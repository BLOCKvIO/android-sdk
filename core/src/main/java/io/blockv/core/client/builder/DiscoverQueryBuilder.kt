/**
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */

package io.blockv.core.client.builder

import org.json.JSONArray
import org.json.JSONObject

open class DiscoverQueryBuilder {

  enum class Scope(val value: String) {
    OWNER("vAtom::vAtomType.owner"),
    TEMPLATE("vAtom::vAtomType.template"),
    TEMPLATE_VARIATION("vAtom::vAtomType.template_variation"),
    ACQUIRABLE("vAtom::vAtomType.acquirable"),
    PARENT_ID("vAtom::vAtomType.parent_id");
  }

  enum class Field(val value: String) {
    ID("id"),
    WHEN_CREATED("when_created"),
    WHEN_MODIFIED("when_modified"),
    PARENT_ID("vAtom::vAtomType.parent_id"),
    OWNER("vAtom::vAtomType.owner"),
    AUTHOR("vAtom::vAtomType.author"),
    TEMPLATE("vAtom::vAtomType.template"),
    TEMPLATE_VARIATION("vAtom::vAtomType.template_variation"),
    DROPPED("vAtom::vAtomType.dropped"),
    TRANSFERABLE("vAtom::vAtomType.transferable"),
    ACQUIRABLE("vAtom::vAtomType.acquirable"),
    CATEGORY("vAtom::vAtomType.category"),
    TITLE("vAtom::vAtomType.title");
  }

  enum class FilterOperation(val operator: String) {
    EQUAL("Eq"),
    GREATER_THAN("Gt"),
    GREATER_OR_EQUAL("Ge"),
    LESS_THAN("Lt"),
    LESS_OR_EQUAL("Le"),
    NOT_EQUAL("Ne"),
    MATCH("Match")
  }

  enum class CombineOperation(val operator: String) {
    AND("And"),
    OR("Or")
  }


  private val json: JSONObject = JSONObject()

  fun setScope(scope: Scope, value: String): DiscoverQueryBuilder {
    json.put("scope", JSONObject().put("key", scope.value).put("value", value))
    return this
  }

  fun addFilter(field: Field, filterOperation: FilterOperation, value: String, combineOperation: CombineOperation): DiscoverQueryBuilder {

    this.addFilter(field.value, filterOperation.operator, value, combineOperation.operator)
    return this
  }

  fun addFilter(field: String, filterOperation: String, value: String, combineOperation: String): DiscoverQueryBuilder {

    if (!json.has("filters")) {
      json.put("filters", JSONArray())
    }

    val filters = json.getJSONArray("filters")

    if (filters.length() == 0) {
      filters.put(JSONObject().put("filter_elems", JSONArray()))
    }

    val elements = filters.getJSONObject(0).getJSONArray("filter_elems")

    elements.put(JSONObject()
      .put("field", field)
      .put("filter_op", filterOperation)
      .put("value", value)
      .put("bool_op", combineOperation))

    return this
  }

  fun build(): JSONObject = json
}