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

class AssetProvider : Model {
  @Serializer.Serialize
  var name: String
  @Serializer.Serialize
  var uri: String
  @Serializer.Serialize
  var type: String
  @Serializer.Serialize
  var descriptor: Map<String, String?>

  @Serializer.Serializable
  constructor(
    name: String,
    uri: String,
    type: String,
    descriptor: Map<String, String?>
  ) {
    this.name = name
    this.uri = uri
    this.type = type
    this.descriptor = descriptor
  }

  override fun toString(): String {
    return "AssetProvider{" +
      " name='" + name + '\'' +
      ", uri='" + uri + '\'' +
      ", type='" + uri + '\'' +
      ", descriptor='" + descriptor + '\'' +
      "}"
  }
}