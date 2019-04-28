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
import android.graphics.Bitmap
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.NetModule
import io.blockv.common.internal.net.rest.auth.Authenticator
import io.blockv.common.internal.net.rest.auth.AuthenticatorImpl
import io.blockv.common.internal.net.rest.auth.JwtDecoderImpl
import io.blockv.common.internal.net.rest.auth.ResourceEncoder
import io.blockv.common.internal.net.rest.auth.ResourceEncoderImpl
import io.blockv.common.internal.net.websocket.WebsocketImpl
import io.blockv.common.internal.repository.DatabaseImpl
import io.blockv.common.internal.repository.Preferences
import io.blockv.common.model.Environment
import io.blockv.common.model.InventoryEvent
import io.blockv.common.model.Model
import io.blockv.common.model.PublicUser
import io.blockv.common.model.Resource
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.Vatom
import io.blockv.common.model.WebSocketEvent
import io.blockv.core.client.manager.ActivityManager
import io.blockv.core.client.manager.ActivityManagerImpl
import io.blockv.core.client.manager.EventManager
import io.blockv.core.client.manager.EventManagerImpl
import io.blockv.core.client.manager.ResourceManager
import io.blockv.core.client.manager.ResourceManagerImpl
import io.blockv.core.client.manager.UserManager
import io.blockv.core.client.manager.UserManagerImpl
import io.blockv.core.client.manager.VatomManager
import io.blockv.core.client.manager.VatomManagerImpl
import io.blockv.core.internal.datapool.GeoMapImpl
import io.blockv.core.internal.datapool.InventoryImpl
import io.blockv.core.internal.repository.mapper.ActionMapper
import io.blockv.core.internal.repository.mapper.FaceMapper
import io.blockv.core.internal.repository.mapper.VatomMapper
import io.blockv.face.client.FaceManager
import io.blockv.face.client.FaceManagerImpl
import io.blockv.faces.ImageFace
import io.blockv.faces.ImageLayeredFace
import io.blockv.faces.ImagePolicyFace
import io.blockv.faces.ImageProgressFace
import io.blockv.faces.WebFace
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import kotlin.reflect.KClass

class Blockv {
  private val preferences: Preferences
  private val auth: Authenticator
  val appId: String
  val netModule: NetModule
  val userManager: UserManager
  val vatomManager: VatomManager
  val resourceManager: ResourceManager
  val activityManager: ActivityManager
  val eventManager: EventManager
  val jsonModule: JsonModule

  init {
    RxJavaPlugins.setErrorHandler { throwable ->
      throwable.printStackTrace()
    }
  }

  private val cacheDir: File

  @Volatile
  private var internalFaceManager: FaceManager? = null
  val faceManager: FaceManager
    get() {
      if (internalFaceManager == null) {
        try {
          val encoder = ResourceEncoderImpl(preferences)

          internalFaceManager = FaceManagerImpl(object : io.blockv.face.client.manager.ResourceManager {
            override val resourceEncoder: ResourceEncoder
              get() = encoder

            override fun getFile(resource: Resource): Single<File> {
              return resourceManager.getFile(resource.url)
            }

            override fun getInputStream(resource: Resource): Single<InputStream> {
              return resourceManager.getInputStream(resource.url)
            }

            override fun getBitmap(resource: Resource): Single<Bitmap> {
              return resourceManager.getBitmap(resource.url)
            }

            override fun getBitmap(resource: Resource, width: Int, height: Int): Single<Bitmap> {
              return resourceManager.getBitmap(resource.url, width, height)
            }

          },
            object : io.blockv.face.client.manager.UserManager {
              override fun getCurrentUser(): Single<PublicUser> {
                return userManager.getCurrentUser()
                  .map {
                    PublicUser(
                      it.id,
                      if (it.isNamePublic) it.firstName else "",
                      if (it.isNamePublic) it.lastName else "",
                      if (it.isAvatarPublic) it.avatarUri else ""
                    )
                  }
              }

              override fun getPublicUser(userId: String): Single<PublicUser> {
                return userManager.getPublicUser(userId)
              }

            },
            object : io.blockv.face.client.manager.VatomManager {
              override fun preformAction(action: String, payload: JSONObject): Single<JSONObject> {
                return vatomManager.preformAction(action, payload)
              }

              override fun getVatoms(vararg ids: String): Single<List<Vatom>> {
                return vatomManager.getVatoms(*ids)
              }

              override fun getInventory(id: String?, page: Int, limit: Int): Single<List<Vatom>> {
                return vatomManager.getInventory(id, page, limit)
              }

            },
            object : io.blockv.face.client.manager.EventManager {
              override fun getVatomStateEvents(): Flowable<WebSocketEvent<StateUpdateEvent>> {
                return eventManager.getVatomStateEvents()
              }

              override fun getInventoryEvents(): Flowable<WebSocketEvent<InventoryEvent>> {
                return eventManager.getInventoryEvents()
              }
            },
            object : io.blockv.face.client.manager.JsonSerializer {
              override fun <T : Model> deserialize(kclass: KClass<T>, json: JSONObject): T? {
                return jsonModule.deserialize(kclass, json)
              }

              override fun <T : Model> serialize(data: T): JSONObject? {
                return jsonModule.serialize(data)
              }

            }
          )

          internalFaceManager!!.registerFace(ImageFace.factory)
          internalFaceManager!!.registerFace(ImageProgressFace.factory)
          internalFaceManager!!.registerFace(ImagePolicyFace.factory)
          internalFaceManager!!.registerFace(ImageLayeredFace.factory)
          internalFaceManager!!.registerFace(WebFace.factory)
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
    this.resourceManager = ResourceManagerImpl(cacheDir, ResourceEncoderImpl(preferences), preferences)
    this.auth = AuthenticatorImpl(this.preferences, jsonModule)
    this.netModule = NetModule(
      auth,
      preferences,
      jsonModule
    )
    val websocket = WebsocketImpl(preferences, jsonModule, auth)
    val database = DatabaseImpl(context, "blockv-datapool.db")
    database.addMapper(ActionMapper())
    database.addMapper(FaceMapper())
    database.addMapper(VatomMapper())
    val inventory = InventoryImpl(netModule.vatomApi, websocket, jsonModule, database)

    this.userManager = UserManagerImpl(
      netModule.userApi,
      auth,
      preferences,
      JwtDecoderImpl(),
      inventory
    )
    this.vatomManager = VatomManagerImpl(netModule.vatomApi, inventory, GeoMapImpl(netModule.vatomApi, websocket))
    this.activityManager = ActivityManagerImpl(netModule.activityApi)
    this.eventManager = EventManagerImpl(websocket, jsonModule)
  }

  constructor(context: Context, environment: Environment) {
    this.cacheDir = context.cacheDir
    this.jsonModule = JsonModule()
    this.appId = environment.appId
    this.preferences = Preferences(context, jsonModule)
    this.preferences.environment = environment
    this.resourceManager = ResourceManagerImpl(cacheDir, ResourceEncoderImpl(preferences), preferences)
    this.auth = AuthenticatorImpl(this.preferences, jsonModule)
    this.netModule = NetModule(auth, preferences, jsonModule)
    val websocket = WebsocketImpl(preferences, jsonModule, auth)
    val database = DatabaseImpl(context, "blockv-datapool.db")
    database.addMapper(ActionMapper())
    database.addMapper(FaceMapper())
    database.addMapper(VatomMapper())
    val inventory = InventoryImpl(netModule.vatomApi, websocket, jsonModule, database)
    this.userManager = UserManagerImpl(
      netModule.userApi,
      auth,
      preferences,
      JwtDecoderImpl(),
      inventory
    )
    this.vatomManager = VatomManagerImpl(netModule.vatomApi, inventory, GeoMapImpl(netModule.vatomApi, websocket))
    this.activityManager = ActivityManagerImpl(netModule.activityApi)
    this.eventManager = EventManagerImpl(websocket, jsonModule)
  }


  constructor(
    context: Context,
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
    this.cacheDir = context.cacheDir
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
    this.eventManager = eventManager
    this.activityManager = activityManager
    this.auth = AuthenticatorImpl(this.preferences, jsonModule)
  }

  class MissingFaceModuleException :
    Exception("Include dependency 'io.blockv.sdk:face:+' to use the face manager.")

}