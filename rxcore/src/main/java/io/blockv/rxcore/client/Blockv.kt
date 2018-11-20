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
package io.blockv.rxcore.client

import android.content.Context
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.NetModule
import io.blockv.common.internal.net.rest.auth.Authenticator
import io.blockv.common.internal.net.rest.auth.AuthenticatorImpl
import io.blockv.common.internal.net.rest.auth.JwtDecoderImpl
import io.blockv.common.internal.net.rest.auth.ResourceEncoderImpl
import io.blockv.common.internal.net.websocket.WebsocketImpl
import io.blockv.common.internal.repository.Preferences
import io.blockv.common.model.*
import io.blockv.common.util.Callable
import io.blockv.faces.ImageFace
import io.blockv.faces.ImageLayeredFace
import io.blockv.faces.ImagePolicyFace
import io.blockv.faces.ImageProgressFace
import io.blockv.rxcore.client.manager.*
import io.blockv.rxface.client.FaceManager
import io.blockv.rxface.client.FaceManagerImpl
import org.json.JSONObject
import java.io.File
import kotlin.reflect.KClass

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

  private val cacheDir: File

  @Volatile
  private var internalEventManager: EventManager? = null
  val eventManager: EventManager
    get() {
      if (internalEventManager == null) {
        try {
          internalEventManager =
            EventManagerImpl(WebsocketImpl(preferences, jsonModule, auth), jsonModule)
        } catch (e: NoClassDefFoundError) {
          throw MissingWebSocketDependencyException()
        } catch (e: Exception) {
          throw MissingWebSocketDependencyException()
        }
      }
      return internalEventManager!!
    }

  @Volatile
  private var internalFaceManager: FaceManager? = null
  val faceManager: FaceManager
    get() {
      if (internalFaceManager == null) {
        try {
          val encoder = ResourceEncoderImpl(preferences)
          internalFaceManager = FaceManagerImpl(io.blockv.rxface.client.ResourceManagerImpl(cacheDir, encoder),
            object : io.blockv.face.client.manager.UserManager {
              override fun getPublicUser(userId: String): Callable<PublicUser?> {
                return Callable.create<PublicUser?> { emitter ->
                  val disposable = userManager.getPublicUser(userId)
                    .subscribe({
                      emitter.onResult(it)
                      emitter.onComplete()
                    }, {
                      emitter.onError(it)
                    })

                  emitter.doOnCompletion {
                    disposable.dispose()
                  }
                }
                  .runOn(Callable.Scheduler.IO)
                  .returnOn(Callable.Scheduler.MAIN)
              }

              override fun getCurrentUser(): Callable<PublicUser?> {
                return Callable.create<PublicUser?> { emitter ->
                  val disposable = userManager.getCurrentUser()
                    .subscribe({
                      if (it == UserManager.NULL_USER) {
                        emitter.onResult(null)
                      } else
                        emitter.onResult(
                          PublicUser(
                            it.id,
                            if (it.isNamePublic) it.firstName else "",
                            if (it.isNamePublic) it.lastName else "",
                            if (it.isAvatarPublic) it.avatarUri else ""
                          )
                        )
                      emitter.onComplete()
                    }, {
                      emitter.onError(it)
                    })

                  emitter.doOnCompletion {
                    disposable.dispose()
                  }
                }
                  .runOn(Callable.Scheduler.IO)
                  .returnOn(Callable.Scheduler.MAIN)
              }

            },
            object : io.blockv.face.client.manager.VatomManager {
              override fun getVatoms(vararg ids: String): Callable<List<Vatom>> {
                return Callable.create<List<Vatom>> { emitter ->
                  val disposable = vatomManager.getVatoms(*ids)
                    .subscribe({
                      emitter.onResult(it)
                      emitter.onComplete()
                    }, {
                      emitter.onError(it)
                    })
                  emitter.doOnCompletion { disposable.dispose() }
                }
                  .runOn(Callable.Scheduler.IO)
                  .returnOn(Callable.Scheduler.MAIN)
              }

              override fun getInventory(id: String?, page: Int, limit: Int): Callable<List<Vatom>> {
                return Callable.create<List<Vatom>> { emitter ->
                  val disposable = vatomManager.getInventory(id, page, limit)
                    .subscribe({
                      emitter.onResult(it)
                      emitter.onComplete()
                    }, {
                      emitter.onError(it)
                    })
                  emitter.doOnCompletion { disposable.dispose() }
                }
                  .runOn(Callable.Scheduler.IO)
                  .returnOn(Callable.Scheduler.MAIN)
              }

            },
            object : io.blockv.face.client.manager.EventManager {
              override fun getVatomStateEvents(): Callable<WebSocketEvent<StateUpdateEvent>> {
                return Callable.create<WebSocketEvent<StateUpdateEvent>> { emitter ->
                  val disposable =
                    eventManager.getVatomStateEvents()
                      .subscribe({
                        emitter.onResult(it)
                        emitter.onComplete()
                      }, {
                        emitter.onError(it)
                      })
                  emitter.doOnCompletion { disposable.dispose() }
                }
                  .runOn(Callable.Scheduler.IO)
                  .returnOn(Callable.Scheduler.MAIN)
              }

              override fun getInventoryEvents(): Callable<WebSocketEvent<InventoryEvent>> {
                return Callable.create<WebSocketEvent<InventoryEvent>> { emitter ->
                  val disposable =
                    eventManager.getInventoryEvents()
                      .subscribe({
                        emitter.onResult(it)
                        emitter.onComplete()
                      }, {
                        emitter.onError(it)
                      })
                  emitter.doOnCompletion { disposable.dispose() }
                }
                  .runOn(Callable.Scheduler.IO)
                  .returnOn(Callable.Scheduler.MAIN)
              }

            },
            object : io.blockv.face.client.manager.JsonSerializer {
              override fun <T : Any> deserialize(kclass: KClass<T>, json: JSONObject): T? {
                return jsonModule.deserialize(kclass, json)
              }

              override fun <T : Any> serialize(data: T): JSONObject? {
                return jsonModule.serialize(data)
              }

            }

          )


          internalFaceManager!!.registerFace(ImageFace.factory)
          internalFaceManager!!.registerFace(ImageProgressFace.factory)
          internalFaceManager!!.registerFace(ImagePolicyFace.factory)
          internalFaceManager!!.registerFace(ImageLayeredFace.factory)
        } catch (e: NoClassDefFoundError) {
          throw MissingFaceModuleException()
        } catch (e: Exception) {
          throw MissingFaceModuleException()
        }
      }
      return internalFaceManager!!
    }

  constructor(context: Context, appId: String) {

    this.cacheDir = context.cacheDir
    this.jsonModule = JsonModule()
    this.appId = appId
    this.preferences = Preferences(context, jsonModule)
    this.preferences.environment = Environment(
      Environment.DEFAULT_SERVER,
      Environment.DEFAULT_WEBSOCKET,
      appId
    )
    this.resourceManager = ResourceManagerImpl(ResourceEncoderImpl(preferences), preferences)
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

    this.cacheDir = context.cacheDir
    this.jsonModule = JsonModule()
    this.appId = environment.appId
    this.preferences = Preferences(context, jsonModule)
    this.preferences.environment = environment
    this.resourceManager = ResourceManagerImpl(ResourceEncoderImpl(preferences), preferences)
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
    this.cacheDir = preferences.context.cacheDir
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

  class MissingWebSocketDependencyException :
    Exception("Include dependency 'com.neovisionaries:nv-websocket-client:2.5' to use the event manager.")

  class MissingFaceModuleException :
    Exception("Include dependency 'io.blockv.sdk:face:+' to use the face manager.")

}