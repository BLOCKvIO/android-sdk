package io.blockv.core.internal.repository.mapper

import io.blockv.common.internal.repository.Mapper
import io.blockv.common.internal.repository.Mapper.Table
import org.json.JSONObject

class ActionMapper : Mapper<JSONObject> {
  override fun model(data: Map<String, Any>): JSONObject {
    return JSONObject(String(android.util.Base64.decode(data["data"] as String, android.util.Base64.DEFAULT)))
  }

  override fun db(data: JSONObject): Table.Row {
    val parts = data.getString("name").split("::Action::")
    return Table.Row.Builder(data.getString("name"))
      .addValue("template", parts[0])
      .addValue("action", parts[1])
      .addValue("data", String(android.util.Base64.encode(data.toString().toByteArray(), android.util.Base64.DEFAULT)))
      .build()
  }

  override val table: Mapper.Table
    get() = Table.Builder("action")
      .addField("template", Table.Type.STRING)
      .addField("action", Table.Type.STRING)
      .addField("data", Table.Type.STRING)
      .build()
}