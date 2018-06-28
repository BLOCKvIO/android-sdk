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
import io.blockv.core.model.vatom.GeoGroup

class GeoGroupDeserializer : Deserializer<GeoGroup?> {

  override fun deserialize(data: org.json.JSONObject): GeoGroup? {
    try {
      val geoHash: String = data.optString("key","")
      val lon: Double = data.optDouble("lon")
      val lat: Double = data.optDouble("lat")
      val count: Int = data.optInt("count")
      return GeoGroup(geoHash, lon, lat, count)
    } catch (e: Exception) {
      Log.e("deserilizer", e.toString())
    }
    return null
  }

}