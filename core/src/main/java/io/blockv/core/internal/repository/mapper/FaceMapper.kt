package io.blockv.core.internal.repository.mapper

import io.blockv.common.internal.repository.Mapper
import io.blockv.common.internal.repository.Mapper.Table
import org.json.JSONObject

class FaceMapper : Mapper<JSONObject> {
  override fun model(data: Map<String, Any>): JSONObject {
    return JSONObject((data["data"] as String))
  }

  override fun db(data: JSONObject): Table.Row {
    return Table.Row.Builder(data.getString("id"))
      .addValue("templateId", data.getString("template"))
      .addValue("data", data.toString().replace("'", "''"))
      .build()
  }

  override val table: Mapper.Table
    get() = Table.Builder("face")
      .addField("templateId", Table.Type.STRING)
      .addField("data", Table.Type.STRING)
      .build()
}