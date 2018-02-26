package io.blockv.core.client

import android.content.Context
import io.blockv.core.client.manager.UserManager
import io.blockv.core.client.manager.UserManagerImpl
import io.blockv.android.core.internal.net.NetModule
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.json.serializer.AssetProviderSerializer
import io.blockv.core.internal.json.serializer.EnviromentSerializer
import io.blockv.android.core.internal.repository.Preferences
import io.blockv.core.model.Action
import io.blockv.core.model.Environment
import io.blockv.core.model.Face
import io.blockv.core.model.Vatom
import io.blockv.core.internal.json.deserializer.*

/**
 * Created by LordCheddar on 2018/02/20.
 */
class Blockv(val userManager: UserManager,
             val preferences: Preferences,
             val appId: String) {

  init {
    preferences.environment = Environment(Environment.Companion.DEFAULT_SERVER, appId)
  }

  companion object {
    fun newDefaultIntsance(context: Context, appId: String): Blockv {
      val vatomDeserilizer: Deserializer<Vatom?> = VatomDeserializer()
      val faceDeserilizer: Deserializer<Face?> = FaceDeserializer()
      val actionDeserilizer: Deserializer<Action?> = ActionDeserializer()
      val jsonModule: JsonModule = JsonModule(
        UserDeserializer(),
        TokenDeserializer(),
        vatomDeserilizer,
        faceDeserilizer,
        actionDeserilizer,
        AssetProviderDeserialzier(),
        AssetProviderSerializer(),
        EnvironmentDeserialzier(),
        EnviromentSerializer(),
        InventoryDeserializer(vatomDeserilizer,faceDeserilizer,actionDeserilizer)
      )
      val preferences: Preferences = Preferences(context, jsonModule)
      val netModule: NetModule = NetModule(preferences, jsonModule)
      return Blockv(UserManagerImpl(netModule.userApi), preferences, appId)
    }
  }
}