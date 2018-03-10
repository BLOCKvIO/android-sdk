package io.blockv.core.internal.json.deserializer

import io.blockv.core.model.AssetProvider
import org.json.JSONObject

/**
 * Created by LordCheddar on 2018/02/24.
 */
class AssetProviderDeserialzier : Deserializer<AssetProvider> {
  override fun deserialize(data: JSONObject): AssetProvider? {
    try {
      val descriptorObject: JSONObject = data.getJSONObject("descriptor")
      val descriptor: HashMap<String, String> = HashMap()
      for (key: String in descriptorObject.keys()) {
        descriptor.put(key, descriptorObject.getString(key))
      }
      return AssetProvider(data.getString("name"),data.getString("uri"), data.getString("type"), descriptor)
    } catch (e: Exception) {
      android.util.Log.w("AssetProvDeserializer", e.message)
    }
    return null
  }
}