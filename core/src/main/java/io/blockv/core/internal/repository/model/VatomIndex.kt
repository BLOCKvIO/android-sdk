package io.blockv.core.internal.repository.model

import org.json.JSONObject

class VatomIndex(
  val id: String,
  val parentId: String,
  val whenModified: String,
  val templateId: String,
  val templateVariationId: String,
  val ownerId: String,
  var data: JSONObject
)