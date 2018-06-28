/**
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.internal.json.deserializer.user


import android.util.Log
import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.PublicUser

class PublicUserDeserializer : Deserializer<PublicUser?> {

  override fun deserialize(data: org.json.JSONObject): PublicUser? {
    try {
      val properties: org.json.JSONObject = data.getJSONObject("properties")
      val id: String? = data.getString("id")
      val firstName: String? = properties.optString("first_name")
      val lastName: String? = properties.optString("last_name")
      val avatarUri: String? = properties.optString("avatar_uri")
      return PublicUser(
        id,
        firstName,
        lastName,
        avatarUri)
    } catch (e: Exception) {
      Log.e("deserilizer", e.toString())
    }
    return null
  }

}