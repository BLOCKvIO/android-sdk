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

package io.blockv.core.client.builder

import org.json.JSONArray
import org.json.JSONObject

/**
 * Builds discover query request payload.
 * This object simplifies the construction of an otherwise involved discover
 * query payload.
 */
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

  enum class ResultType(val result: String) {
    PAYLOAD("*"),
    COUNT("count")
  }

  private val json: JSONObject = JSONObject()

  /**
   * Sets the scope of the search query.
   *
   * A scope must always be supplied. Scopes are defined using a `key` and `value`.
   * The key specifies the property of the vAtom to search. The value is the search term.
   *
   * @param scope is the search field.
   * @param value is the lookup.
   *
   * @return DiscoverQueryBuilder.
   */
  fun setScope(scope: Scope, value: String): DiscoverQueryBuilder {
    json.put("scope", JSONObject().put("key", scope.value).put("value", value))
    return this
  }

  /**
   * Sets the scope of the search query to the current user.
   *
   * @return DiscoverQueryBuilder.
   */
  fun setScopeToOwner(): DiscoverQueryBuilder {
    json.put("scope", JSONObject().put("key", "vAtom::vAtomType.owner").put("value", "\$currentuser"))
    return this
  }

  /**
   * Adds a defined filter element to the query.
   *
   * Filter elements, similar to scopes, are defined using a `field` and `value`. However, filters
   * offer more flexibility because they allow a *filter operator* to be supplied, e.g. `GREATER_THAN` which
   * filters those vAtoms whose value is greater than the supplied `value`. The combine operator is
   * applied *between* filter elements.
   *
   * @param field is the property to search.
   * @param filterOperation is the operator to apply between the `field` and `value` items.
   * @param value is for lookup
   * @param combineOperation controls the boolean operator applied between this element and the other filter elements
   * @return DiscoverQueryBuilder.
   */
  fun addFilter(
    field: Field,
    filterOperation: FilterOperation,
    value: String,
    combineOperation: CombineOperation
  ): DiscoverQueryBuilder {
    this.addFilter(field.value, filterOperation.operator, value, combineOperation.operator)
    return this
  }

  /**
   * Adds a custom filter element to the query.
   *
   * This method provides you with full control over the contents of the filter element.
   *
   * @param field is the property to search.
   * @param filterOperation is the operator to apply between the `field` and `value` items.
   * @param value is for lookup
   * @param combineOperation controls the boolean operator applied between this element and the other filter elements
   * @return DiscoverQueryBuilder.
   */
  fun addFilter(field: String, filterOperation: String, value: String, combineOperation: String): DiscoverQueryBuilder {

    if (!json.has("filters")) {
      json.put("filters", JSONArray())
    }

    val filters = json.getJSONArray("filters")

    if (filters.length() == 0) {
      filters.put(JSONObject().put("filter_elems", JSONArray()))
    }

    val elements = filters.getJSONObject(0).getJSONArray("filter_elems")

    elements.put(
      JSONObject()
        .put("field", field)
        .put("filter_op", filterOperation)
        .put("value", value)
        .put("bool_op", combineOperation)
    )

    return this
  }

  /**
   * Sets the return type.
   *
   * @param type controls the response payload of the query
   *             - `*` returns vAtoms.
   *             - `count` returns only the numerical count of the query and an empty vAtom array.
   * @return DiscoverQueryBuilder.
   */
  fun setReturn(type: ResultType): DiscoverQueryBuilder {
    json.put(
      "return",
      JSONObject()
        .put("type", type.result)
        .put("fields", JSONArray())
    )
    return this
  }

  /**
   * Returns the Json discover query.
   *
   * @return JSONObject.
   */
  fun build(): JSONObject {
    if (!json.has("return")) {
      json.put(
        "return",
        JSONObject()
          .put("type", ResultType.PAYLOAD)
          .put("fields", JSONArray())
      )
    }
    return json
  }
}