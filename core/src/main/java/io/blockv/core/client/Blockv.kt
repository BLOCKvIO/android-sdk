package io.blockv.core.client

import android.content.Context
import io.blockv.android.core.internal.net.NetModule
import io.blockv.android.core.internal.repository.Preferences
import io.blockv.core.client.manager.UserManager
import io.blockv.core.client.manager.UserManagerImpl
import io.blockv.core.client.manager.VatomManager
import io.blockv.core.client.manager.VatomManagerImpl
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.json.deserializer.*
import io.blockv.core.internal.json.serializer.AssetProviderSerializer
import io.blockv.core.internal.json.serializer.EnviromentSerializer
import io.blockv.core.model.Action
import io.blockv.core.model.Environment
import io.blockv.core.model.Face
import io.blockv.core.model.Vatom

/**
 * Created by LordCheddar on 2018/02/20.
 */
class Blockv {

  val appId:String
  private val preferences:Preferences
  private val netModule: NetModule
  private val jsonModule:JsonModule
  val userManager:UserManager
  val vatomManager:VatomManager

  constructor(context: Context, appId: String) {
    val vatomDeserilizer: Deserializer<Vatom?> = VatomDeserializer()
    val faceDeserilizer: Deserializer<Face?> = FaceDeserializer()
    val actionDeserilizer: Deserializer<Action?> = ActionDeserializer()
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
      InventoryDeserializer(vatomDeserilizer, faceDeserilizer, actionDeserilizer)
    )
    this.appId = appId
    this.preferences = Preferences(context, jsonModule)
    this.preferences.environment = Environment(Environment.Companion.DEFAULT_SERVER, appId)
    this.netModule = NetModule(preferences, jsonModule)
    this.userManager = UserManagerImpl(netModule.userApi)
    this.vatomManager = VatomManagerImpl(netModule.vatomApi)
  }


  constructor(appId: String,
              preferences: Preferences,
              jsonModule:JsonModule,
              netModule: NetModule,
              userManager: UserManager,
              vatomManager: VatomManager) {
    this.appId = appId
    this.preferences = preferences
    this.preferences.environment = Environment(Environment.Companion.DEFAULT_SERVER, appId)
    this.jsonModule = jsonModule
    this.netModule = netModule
    this.userManager = userManager
    this.vatomManager = vatomManager

  }

}