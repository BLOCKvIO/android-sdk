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

class GeoRequest(val latitude: Double, val longitude: Double, val radius: Int, val limit: Int) {

  fun toJson(): JSONObject {
    val out = JSONObject()
    out.put("unit", "m")
    out.put("radius",radius)
    out.put("limit",limit)
    out.put("center_geo_pos",JSONObject().put("Lat",latitude).put("Lon",longitude))

    return out
  }
}