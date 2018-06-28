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
package io.blockv.core.client

import android.content.Context
import io.blockv.core.client.manager.*
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.json.deserializer.*
import io.blockv.core.internal.json.serializer.AssetProviderSerializer
import io.blockv.core.internal.json.serializer.EnviromentSerializer
import io.blockv.core.internal.json.serializer.JwtSerializer
import io.blockv.core.internal.net.NetModule
import io.blockv.core.internal.net.rest.auth.AuthenticatorImpl
import io.blockv.core.internal.net.rest.auth.JwtDecoderImpl
import io.blockv.core.internal.net.websocket.WebsocketImpl
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.model.*

class Blockv {

  val appId: String
  private val preferences: Preferences
  val netModule: NetModule
  private val jsonModule: JsonModule
  val userManager: UserManager
  val vatomManager: VatomManager
  val resourceManager: ResourceManager
  val eventManager: EventManager

  constructor(context: Context, appId: String) {
    val vatomDeserilizer: Deserializer<Vatom?> = VatomDeserializer()
    val faceDeserilizer: Deserializer<Face?> = FaceDeserializer()
    val actionDeserilizer: Deserializer<Action?> = ActionDeserializer()
    val messageDeserializer:Deserializer<ActivityMessage?> = ActivityMessageDeserializer()
    this.jsonModule = JsonModule(
      UserDeserializer(),
      TokenDeserializer(),
      vatomDeserilizer,
      faceDeserilizer,
      actionDeserilizer,
      AssetProviderDeserialzier(),
      AssetProviderSerializer(),
      EnvironmentDeserialzier(),
      EnviromentSerializer(),
      InventoryDeserializer(vatomDeserilizer, faceDeserilizer, actionDeserilizer),
      JwtDeserializer(),
      JwtSerializer(),
      DiscoverGroupDeserializer(vatomDeserilizer, faceDeserilizer, actionDeserilizer),
      PublicUserDeserializer(),
      GeoGroupDeserializer(),
      InventoryEventDeserializer(),
      StateEventDeserializer(),
      ActivityEventDeserializer(),
      WebsocketEventDeserializer(),
      ActivityThreadListDeserializer(ActivityThreadDeserializer(messageDeserializer)),
      ActivityMessageListDeserializer(messageDeserializer)
    )
    this.appId = appId
    this.preferences = Preferences(context, jsonModule)
    this.preferences.environment = Environment(
      Environment.DEFAULT_SERVER,
      Environment.DEFAULT_WEBSOCKET,
      appId)
    this.resourceManager = ResourceManagerImpl(preferences)
    val auth = AuthenticatorImpl(this.preferences, jsonModule)
    this.netModule = NetModule(
      auth,
      preferences,
      jsonModule)
    this.userManager = UserManagerImpl(
      netModule.userApi,
      auth,
      preferences,
      JwtDecoderImpl()
    )
    this.vatomManager = VatomManagerImpl(netModule.vatomApi)
    this.eventManager = EventManagerImpl(WebsocketImpl(preferences, jsonModule, auth), jsonModule)
  }

  constructor(context: Context, environment: Environment) {
    val vatomDeserilizer: Deserializer<Vatom?> = VatomDeserializer()
    val faceDeserilizer: Deserializer<Face?> = FaceDeserializer()
    val actionDeserilizer: Deserializer<Action?> = ActionDeserializer()
    val messageDeserializer:Deserializer<ActivityMessage?> = ActivityMessageDeserializer()
    this.jsonModule = JsonModule(
      UserDeserializer(),
      TokenDeserializer(),
      vatomDeserilizer,
      faceDeserilizer,
      actionDeserilizer,
      AssetProviderDeserialzier(),
      AssetProviderSerializer(),
      EnvironmentDeserialzier(),
      EnviromentSerializer(),
      InventoryDeserializer(vatomDeserilizer, faceDeserilizer, actionDeserilizer),
      JwtDeserializer(),
      JwtSerializer(),
      DiscoverGroupDeserializer(vatomDeserilizer, faceDeserilizer, actionDeserilizer),
      PublicUserDeserializer(),
      GeoGroupDeserializer(),
      InventoryEventDeserializer(),
      StateEventDeserializer(),
      ActivityEventDeserializer(),
      WebsocketEventDeserializer(),
      ActivityThreadListDeserializer(ActivityThreadDeserializer(messageDeserializer)),
      ActivityMessageListDeserializer(messageDeserializer)
    )
    this.appId = environment.appId
    this.preferences = Preferences(context, jsonModule)
    this.preferences.environment = environment
    this.resourceManager = ResourceManagerImpl(preferences)
    val auth = AuthenticatorImpl(this.preferences, jsonModule)
    this.netModule = NetModule(auth, preferences, jsonModule)
    this.userManager = UserManagerImpl(
      netModule.userApi,
      auth,
      preferences,
      JwtDecoderImpl())
    this.vatomManager = VatomManagerImpl(netModule.vatomApi)
    this.eventManager = EventManagerImpl(WebsocketImpl(preferences, jsonModule, auth), jsonModule)
  }


  constructor(appId: String,
              preferences: Preferences,
              jsonModule: JsonModule,
              netModule: NetModule,
              userManager: UserManager,
              vatomManager: VatomManager,
              eventManager: EventManager,
              resourceManager: ResourceManager) {
    this.appId = appId
    this.preferences = preferences
    this.preferences.environment = Environment(
      Environment.DEFAULT_SERVER,
      Environment.DEFAULT_WEBSOCKET,
      appId)
    this.jsonModule = jsonModule
    this.netModule = netModule
    this.userManager = userManager
    this.vatomManager = vatomManager
    this.resourceManager = resourceManager
    this.eventManager = eventManager
  }

}