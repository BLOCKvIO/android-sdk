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
  val jwtSerializer: Serializer<Jwt?>


)