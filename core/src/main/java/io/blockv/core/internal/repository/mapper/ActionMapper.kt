package io.blockv.core.internal.repository.mapper

import io.blockv.common.internal.repository.Mapper
import io.blockv.common.internal.repository.Mapper.Table
import org.json.JSONObject

class ActionMapper : Mapper<JSONObject> {
  override fun model(data: Map<String, Any>): JSONObject {
    return JSONObject(data["data"] as String)
  }

  override fun db(data: JSONObject): Table.Row {
    val parts = data.getString("name").split("::Action::")
    return Table.Row.Builder(data.getString("name"))
      .addValue("template", parts[0])
      .addValue("action", parts[1])
      .addValue("data", data.toString().replace("'", "''"))
      .build()
  }

  override val table: Mapper.Table
    get() = Table.Builder("action")
      .addField("template", Table.Type.STRING)
      .addField("action", Table.Type.STRING)
      .addField("data", Table.Type.STRING)
      .build()
}