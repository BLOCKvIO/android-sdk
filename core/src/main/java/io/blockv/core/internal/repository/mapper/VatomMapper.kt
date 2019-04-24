package io.blockv.core.internal.repository.mapper

import io.blockv.common.internal.repository.Mapper
import io.blockv.common.internal.repository.Mapper.Table
import io.blockv.common.internal.repository.Mapper.Table.Row
import io.blockv.core.internal.repository.model.VatomIndex
import org.json.JSONObject

open class VatomMapper : Mapper<VatomIndex> {

  override fun model(data: Map<String, Any>): VatomIndex {
    return VatomIndex(
      data["_id"] as String,
      data["parent_id"] as String,
      data["when_modified"] as String,
      data["template"] as String,
      data["template_variation"] as String,
      data["owner_id"] as String,
      JSONObject(data["data"] as String)
    )
  }

  override fun db(data: VatomIndex): Mapper.Table.Row {
    return Row.Builder(data.id)
      .addValue("parent_id", data.parentId)
      .addValue("when_modified", data.whenModified)
      .addValue("template", data.templateId)
      .addValue("template_variation", data.templateVariationId)
      .addValue("owner_id", data.ownerId)
      .addValue(
        "data", data.data.toString().replace("'", "''")
      )
      .build()
  }

  override val table: Mapper.Table
    get() = Table.Builder("vatom")
      .addField("parent_id", Table.Type.STRING)
      .addField("when_modified", Table.Type.STRING)
      .addField("template", Table.Type.STRING)
      .addField("template_variation", Table.Type.STRING)
      .addField("owner_id", Table.Type.STRING)
      .addField("data", Table.Type.STRING)
      .build()
}