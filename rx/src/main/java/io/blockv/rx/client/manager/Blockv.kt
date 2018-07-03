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
package io.blockv.rx.client.manager

import android.content.Context
import io.blockv.core.client.manager.ResourceManager
import io.blockv.core.client.manager.ResourceManagerImpl
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.json.deserializer.Deserializer
import io.blockv.core.internal.json.deserializer.EnvironmentDeserialzier
import io.blockv.core.internal.json.deserializer.JwtDeserializer
import io.blockv.core.internal.json.deserializer.activity.ActivityMessageDeserializer
import io.blockv.core.internal.json.deserializer.activity.ActivityMessageListDeserializer
import io.blockv.core.internal.json.deserializer.activity.ActivityThreadDeserializer
import io.blockv.core.internal.json.deserializer.activity.ActivityThreadListDeserializer
import io.blockv.core.internal.json.deserializer.event.ActivityEventDeserializer
import io.blockv.core.internal.json.deserializer.event.InventoryEventDeserializer
import io.blockv.core.internal.json.deserializer.event.StateEventDeserializer
import io.blockv.core.internal.json.deserializer.event.WebsocketEventDeserializer
import io.blockv.core.internal.json.deserializer.resource.AssetProviderDeserialzier
import io.blockv.core.internal.json.deserializer.user.PublicUserDeserializer
import io.blockv.core.internal.json.deserializer.user.TokenDeserializer
import io.blockv.core.internal.json.deserializer.user.UserDeserializer
import io.blockv.core.internal.json.deserializer.vatom.*
import io.blockv.core.internal.json.serializer.user.AssetProviderSerializer
import io.blockv.core.internal.json.serializer.user.EnviromentSerializer
import io.blockv.core.internal.json.serializer.user.JwtSerializer
import io.blockv.core.internal.net.NetModule
import io.blockv.core.internal.net.rest.auth.Authenticator
import io.blockv.core.internal.net.rest.auth.AuthenticatorImpl
import io.blockv.core.internal.net.rest.auth.JwtDecoderImpl
import io.blockv.core.internal.net.websocket.WebsocketImpl
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.model.*

class Blockv {

  val appId: String
  private val preferences: Preferences
  private val netModule: NetModule
  private val jsonModule: JsonModule
  private val auth: Authenticator
  val userManager: UserManager
  val vatomManager: VatomManager
  val resourceManager: ResourceManager
  val activityManager: ActivityManager
  @Volatile
  private var internalEventManager: EventManager? = null
  val eventManager: EventManager
    get() {
      if (internalEventManager == null) {
        try {
          internalEventManager = EventManagerImpl(WebsocketImpl(preferences, jsonModule, auth), jsonModule)
        } catch (e: NoClassDefFoundError) {
          throw io.blockv.core.client.manager.EventManager.MissingWebSocketDependencyException()
        } catch (e: Exception) {
          throw io.blockv.core.client.manager.EventManager.MissingWebSocketDependencyException()
        }
      }
      return internalEventManager!!
    }

  constructor(context: Context, appId: String) {
    val vatomDeserilizer: Deserializer<Vatom?> = VatomDeserializer()
    val faceDeserilizer: Deserializer<Face?> = FaceDeserializer()
    val actionDeserilizer: Deserializer<Action?> = ActionDeserializer()
    val messageDeserializer: Deserializer<ActivityMessage?> = ActivityMessageDeserializer()
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
      appId
    )
    this.resourceManager = ResourceManagerImpl(preferences)
    this.auth = AuthenticatorImpl(preferences, jsonModule)
    this.netModule = NetModule(
      auth,
      preferences,
      jsonModule
    )
    this.userManager = UserManagerImpl(
      netModule.userApi,
      preferences,
      JwtDecoderImpl()
    )
    this.vatomManager = VatomManagerImpl(netModule.vatomApi)
    this.activityManager = ActivityManagerImpl(netModule.activityApi)
  }

  constructor(context: Context, environment: Environment) {
    val vatomDeserilizer: Deserializer<Vatom?> = VatomDeserializer()
    val faceDeserilizer: Deserializer<Face?> = FaceDeserializer()
    val actionDeserilizer: Deserializer<Action?> = ActionDeserializer()
    val messageDeserializer: Deserializer<ActivityMessage?> = ActivityMessageDeserializer()
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
    this.auth = AuthenticatorImpl(preferences, jsonModule)
    this.netModule = NetModule(auth, preferences, jsonModule)
    this.userManager = UserManagerImpl(
      netModule.userApi,
      preferences,
      JwtDecoderImpl()
    )
    this.vatomManager = VatomManagerImpl(netModule.vatomApi)
    this.activityManager = ActivityManagerImpl(netModule.activityApi)
  }

  constructor(
    appId: String,
    preferences: Preferences,
    jsonModule: JsonModule,
    netModule: NetModule,
    userManager: UserManager,
    vatomManager: VatomManager,
    activityManager: ActivityManager,
    eventManager: EventManager,
    resourceManager: ResourceManager
  ) {
    this.appId = appId
    this.preferences = preferences
    this.preferences.environment = Environment(
      Environment.DEFAULT_SERVER,
      Environment.DEFAULT_WEBSOCKET,
      appId
    )
    this.jsonModule = jsonModule
    this.netModule = netModule
    this.userManager = userManager
    this.vatomManager = vatomManager
    this.internalEventManager = eventManager
    this.resourceManager = resourceManager
    this.activityManager = activityManager
    this.auth = AuthenticatorImpl(preferences, jsonModule)
  }

}