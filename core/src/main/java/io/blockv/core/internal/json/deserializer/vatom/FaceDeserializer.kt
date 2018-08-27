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
package io.blockv.core.internal.json.deserializer.vatom

import android.util.Log
import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.model.Face
import io.blockv.core.model.FaceProperty
import org.json.JSONArray
import org.json.JSONObject

class FaceDeserializer : Deserializer<Face> {
  override fun deserialize(data: JSONObject): Face? {
    try {
      val meta: JSONObject = data.getJSONObject("meta")
      val properties: JSONObject = data.getJSONObject("properties")
      val id: String = data.getString("id")
      val template: String = data.getString("template")
      val createdBy: String? = meta.optString("created_by")
      val whenCreated: String? = meta.getString("when_created")
      val whenModified: String? = meta.optString("when_modified", whenCreated)
      val displayUrl: String = properties.getString("display_url")
      val constraints: JSONObject = properties.optJSONObject("constraints")
      val resourceArray = properties.optJSONArray("resources")
      val config = properties.optJSONObject("config")
      val resources: ArrayList<String> = ArrayList(resourceArray.length())
      (0..resourceArray.length())
        .mapTo(resources) {
          resourceArray.optString(it)
        }

      return Face(
        id,
        template,
        createdBy,
        whenCreated,
        whenModified,
        FaceProperty(
          displayUrl,
          constraints.optString("view_mode"),
          constraints.optString("platform"),
          config,
          resources
        )
      )
    } catch (e: Exception) {
      Log.w("FaceDeserializer", e.message)
    }
    return null
  }

}