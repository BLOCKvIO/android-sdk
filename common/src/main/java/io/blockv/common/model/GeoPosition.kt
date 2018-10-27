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

import io.blockv.common.internal.json.JsonModule

class GeoPosition {

  @JsonModule.Serialize
  var type: String?
  @JsonModule.Serialize(name = "\$reql_type\$")
  var reqType: String?
  @JsonModule.Serialize
  var coordinates: List<Float>?

  @JsonModule.Serializable
  constructor(type: String?, reqType: String?, coordinates: List<Float>?) {
    this.type = type
    this.reqType = reqType
    this.coordinates = coordinates
  }

  constructor(geoPosition: GeoPosition) : this(
    geoPosition.type,
    geoPosition.reqType,
    ArrayList(geoPosition.coordinates)
  )

  override fun toString(): String {
    return "GeoPos{" +
      "type='" + type + '\'' +
      ", reqType='" + reqType + '\'' +
      ", coordinates=" + coordinates +
      '}'
  }
}
