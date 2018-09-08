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
package io.blockv.core.client

import android.content.Context
import io.blockv.core.client.manager.*
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.json.deserializer.EnvironmentDeserialzier
import io.blockv.common.internal.json.deserializer.JwtDeserializer
import io.blockv.common.internal.json.deserializer.activity.ActivityMessageDeserializer
import io.blockv.common.internal.json.deserializer.activity.ActivityMessageListDeserializer
import io.blockv.common.internal.json.deserializer.activity.ActivityThreadDeserializer
import io.blockv.common.internal.json.deserializer.activity.ActivityThreadListDeserializer
import io.blockv.common.internal.json.deserializer.event.ActivityEventDeserializer
import io.blockv.common.internal.json.deserializer.event.InventoryEventDeserializer
import io.blockv.common.internal.json.deserializer.event.StateEventDeserializer
import io.blockv.common.internal.json.deserializer.event.WebsocketEventDeserializer
import io.blockv.common.internal.json.deserializer.resource.AssetProviderDeserialzier
import io.blockv.common.internal.json.deserializer.user.PublicUserDeserializer
import io.blockv.common.internal.json.deserializer.user.TokenDeserializer
import io.blockv.common.internal.json.deserializer.user.UserDeserializer
import io.blockv.common.internal.json.deserializer.vatom.*
import io.blockv.common.internal.json.serializer.user.AssetProviderSerializer
import io.blockv.common.internal.json.serializer.user.EnviromentSerializer
import io.blockv.common.internal.json.serializer.user.JwtSerializer
import io.blockv.common.internal.net.NetModule
import io.blockv.common.internal.net.rest.auth.Authenticator
import io.blockv.common.internal.net.rest.auth.AuthenticatorImpl
import io.blockv.common.internal.net.rest.auth.JwtDecoderImpl
import io.blockv.common.internal.net.websocket.WebsocketImpl
import io.blockv.common.internal.repository.Preferences
import io.blockv.common.model.Environment

class Blockv {
  private val preferences: Preferences
  private val jsonModule: JsonModule
  private val auth: Authenticator
  val appId: String
  val netModule: NetModule
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
          throw EventManager.MissingWebSocketDependencyException()
        } catch (e: Exception) {
          throw EventManager.MissingWebSocketDependencyException()
        }
      }
      return internalEventManager!!
    }

  constructor(context: Context, appId: String) {

    val faceDeserializer = FaceDeserializer()
    val actionDeserializer = ActionDeserializer()
    val vatomDeserializer = VatomDeserializer(faceDeserializer, actionDeserializer)
    val inventoryDeserializer = InventoryDeserializer(vatomDeserializer, faceDeserializer, actionDeserializer)
    val messageDeserializer = ActivityMessageDeserializer()
    this.jsonModule = JsonModule(
      UserDeserializer(),
      TokenDeserializer(),
      vatomDeserializer,
      faceDeserializer,
      actionDeserializer,
      AssetProviderDeserialzier(),
      AssetProviderSerializer(),
      EnvironmentDeserialzier(),
      EnviromentSerializer(),
      inventoryDeserializer,
      JwtDeserializer(),
      JwtSerializer(),
      DiscoverDeserializer(inventoryDeserializer),
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
    this.auth = AuthenticatorImpl(this.preferences, jsonModule)
    this.netModule = NetModule(
      auth,
      preferences,
      jsonModule
    )
    this.userManager = UserManagerImpl(
      netModule.userApi,
      auth,
      preferences,
      JwtDecoderImpl()
    )
    this.vatomManager = VatomManagerImpl(netModule.vatomApi)
    this.activityManager = ActivityManagerImpl(netModule.activityApi)
  }

  constructor(context: Context, environment: Environment) {
    val faceDeserializer = FaceDeserializer()
    val actionDeserializer = ActionDeserializer()
    val vatomDeserializer = VatomDeserializer(faceDeserializer, actionDeserializer)
    val inventoryDeserializer = InventoryDeserializer(vatomDeserializer, faceDeserializer, actionDeserializer)
    val messageDeserializer = ActivityMessageDeserializer()
    this.jsonModule = JsonModule(
      UserDeserializer(),
      TokenDeserializer(),
      vatomDeserializer,
      faceDeserializer,
      actionDeserializer,
      AssetProviderDeserialzier(),
      AssetProviderSerializer(),
      EnvironmentDeserialzier(),
      EnviromentSerializer(),
      inventoryDeserializer,
      JwtDeserializer(),
      JwtSerializer(),
      DiscoverDeserializer(inventoryDeserializer),
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
    this.auth = AuthenticatorImpl(this.preferences, jsonModule)
    this.netModule = NetModule(auth, preferences, jsonModule)
    this.userManager = UserManagerImpl(
      netModule.userApi,
      auth,
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
    this.resourceManager = resourceManager
    this.internalEventManager = eventManager
    this.activityManager = activityManager
    this.auth = AuthenticatorImpl(this.preferences, jsonModule)
  }

}