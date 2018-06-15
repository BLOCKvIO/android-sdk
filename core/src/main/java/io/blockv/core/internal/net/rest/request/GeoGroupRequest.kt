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
package io.blockv.core.internal.net.rest.request

import org.json.JSONObject

class GeoGroupRequest(val bottomLeftLon: Double,
                      val bottomLeftLat: Double,
                      val topRightLon: Double,
                      val topRightLat: Double,
                      val precision: Int,
                      val filter: String) {

  fun toJson(): JSONObject {
    val out = JSONObject()
    out.put("precision", precision)
    out.put("filter", filter)
    out.put("bottom_left", JSONObject().put("lat", bottomLeftLat).put("lon", bottomLeftLon))
    out.put("top_right", JSONObject().put("lat", topRightLat).put("lon", topRightLon))
    return out
  }

}