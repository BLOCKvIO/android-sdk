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
package io.blockv.core.internal.json

import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.internal.json.serializer.Serializer
import io.blockv.core.model.*

class JsonModule(
  val userDeserilizer: Deserializer<User?>,
  val tokenDeserilizer: Deserializer<Token?>,
  val vatomDeserilizer: Deserializer<Vatom?>,
  val faceDeserilizer: Deserializer<Face?>,
  val actionDeserilizer: Deserializer<Action?>,
  val assetProviderDeserializer: Deserializer<AssetProvider?>,
  val assetProviderSerializer: Serializer<AssetProvider?>,
  val environmentDeserializer: Deserializer<Environment?>,
  val environmentSerializer: Serializer<Environment?>,
  val groupDeserilizer: Deserializer<Group?>,
  val jwtDeserilizer: Deserializer<Jwt?>,
  val jwtSerializer: Serializer<Jwt?>,
  val discoverDeserilizer: Deserializer<DiscoverGroup?>


)