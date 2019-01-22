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
package io.blockv.common.model

import io.blockv.common.internal.json.serializer.Serializer

class GeoGroup : Model {

  @Serializer.Serialize(name = "key")
  val geoHash: String
  @Serializer.Serialize(name = "lon")
  val longitude: Double
  @Serializer.Serialize(name = "lat")
  val latitude: Double
  @Serializer.Serialize(name = "count")
  val count: Int

  @Serializer.Serializable
  constructor(geoHash: String, longitude: Double, latitude: Double, count: Int) {
    this.geoHash = geoHash
    this.longitude = longitude
    this.latitude = latitude
    this.count = count
  }

  override fun toString(): String {
    return "GeoGroup{" +
      " geoHash='" + geoHash + '\'' +
      ", longitude='" + longitude + '\'' +
      ", latitude='" + latitude + '\'' +
      ", count='" + count + '\'' +
      "}"
  }
}