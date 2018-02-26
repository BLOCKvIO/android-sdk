package io.blockv.core.internal.json.serializer

import io.blockv.core.model.AssetProvider
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/24.
 */
class AssetProviderSerializer : Serializer<AssetProvider?> {
  override fun serialize(data: AssetProvider?): JSONObject {
    val out: JSONObject = JSONObject()
    if(data!=null) {
      out.put("name", data.name)
      out.put("type", data.type)
      val descriptor: JSONObject = JSONObject()
      for (key: String in data.descriptor.keys) {
        descriptor.put(key, data.descriptor.getValue(key))
      }
      out.put("descriptor", descriptor)
    }
    return out
  }
}