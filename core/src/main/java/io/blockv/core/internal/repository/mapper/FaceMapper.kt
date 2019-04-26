package io.blockv.core.internal.repository.mapper

import io.blockv.common.internal.repository.Mapper
import io.blockv.common.internal.repository.Mapper.Table
import org.json.JSONObject

class FaceMapper : Mapper<JSONObject> {
  override fun model(data: Map<String, Any>): JSONObject {
    return JSONObject(String(android.util.Base64.decode((data["data"] as String).toByteArray(), android.util.Base64.DEFAULT)))
  }

  override fun db(data: JSONObject): Table.Row {
    return Table.Row.Builder(data.getString("id"))
      .addValue("templateId", data.getString("template"))
      .addValue(
        "data", String(android.util.Base64.encode(data.toString().toByteArray(), android.util.Base64.DEFAULT))
      )
      .build()
  }

  override val table: Mapper.Table
    get() = Table.Builder("face")
      .addField("templateId", Table.Type.STRING)
      .addField("data", Table.Type.STRING)
      .build()
}