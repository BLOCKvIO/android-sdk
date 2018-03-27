package io.blockv.core.client.builder

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/03/27.
 */
class DiscoverQueryBuilder {

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
    GREATERTHAN("Gt"),
    GREATEROREQUAL("Ge"),
    LESSTHAN("Lt"),
    LESSOREQUAL("Le"),
    NOTEQUAL("Ne"),
    MATCH("Match")
  }

  enum class CombineOperation(val operator: String) {
    AND("And"),
    OR("Or")
  }

  companion object {
    val CURRENT_USER: String = "\$currentuser"
  }

  private val json: JSONObject = JSONObject()

  fun setScope(scope: Scope, value: String) {
    json.put("scope", JSONObject().put("key", scope.value).put("value", value));
  }

  fun addFilter(field: Field, filterOperation: FilterOperation, value: String, combineOperation: CombineOperation) {

    this.addFilter(field.value, filterOperation.operator, value, combineOperation.operator)
  }

  fun addFilter(field: String, filterOperation: String, value: String, combineOperation: String) {

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
  }

  fun build(): JSONObject = json
}