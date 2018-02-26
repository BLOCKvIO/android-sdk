package io.blockv.core.internal.json.deserializer

/**
 * Created by LordCheddar on 2018/02/26.
 */
import android.util.Log
import io.blockv.core.model.User

class UserDeserializer : Deserializer<User?> {

  override fun deserialize(data: org.json.JSONObject): User? {
    try {
      val meta: org.json.JSONObject = data.getJSONObject("meta")
      val properties: org.json.JSONObject = data.getJSONObject("properties")
      val id: String? = data.getString("id")
      val whenCreated: String? = meta.getString("when_created")
      val whenModified: String? = meta.getString("when_modified")
      val firstName: String? = properties.optString("first_name")
      val lastName: String? = properties.optString("last_name")
      val avatarUri: String? =  properties.optString("avatar_uri")
      val birthday: String? = properties.optString("birthday")
      val language: String? = properties.optString("language")

      return User(
        id,
        whenCreated,
        whenModified,
        firstName,
        lastName,
        avatarUri,
        birthday,
        language)
    } catch (e: Exception) {
      Log.e("deserilizer",e.toString())
    }
    return null
  }

}