/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.common.internal.json.deserializer.user


import android.util.Log
import io.blockv.common.internal.json.deserializer.Deserializer
import io.blockv.common.model.User
import org.json.JSONObject
import kotlin.reflect.KClass

class UserDeserializer : Deserializer<User>() {

  override fun deserialize(
    type: KClass<*>,
    data: JSONObject,
    deserializers: Map<KClass<*>, Deserializer<*>>
  ): User? {
    try {
      val meta: org.json.JSONObject = data.getJSONObject("meta")
      val properties: org.json.JSONObject = data.getJSONObject("properties")
      val id: String = data.getString("id")
      val whenCreated: String = meta.getString("when_created")
      val whenModified: String = meta.getString("when_modified")
      val firstName: String = properties.optString("first_name", "")
      val lastName: String = properties.optString("last_name", "")
      val avatarUri: String = properties.optString("avatar_uri", "")
      val birthday: String = properties.optString("birthday", "")
      val language: String = properties.optString("language", "")
      val namePublic: Boolean = properties.optBoolean("name_public")
      val avatarPublic: Boolean = properties.optBoolean("avatar_public")
      val passwordSet: Boolean = properties.optBoolean("is_password_set")
      val nonPush: Boolean = properties.optBoolean("nonpush_notification")

      return User(
        id,
        whenCreated,
        whenModified,
        firstName,
        lastName,
        avatarUri,
        birthday,
        language,
        namePublic,
        avatarPublic,
        passwordSet,
        nonPush
      )
    } catch (e: Exception) {
      Log.e("deserilizer", e.toString())
    }
    return null
  }

}