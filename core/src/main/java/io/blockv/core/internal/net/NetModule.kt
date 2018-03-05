package io.blockv.core.internal.net

import io.blockv.core.internal.net.rest.Client
import io.blockv.core.internal.net.rest.HttpClient
import io.blockv.core.internal.repository.Preferences
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.api.UserApi
import io.blockv.core.internal.net.rest.api.UserApiImpl
import io.blockv.core.internal.net.rest.api.VatomApi
import io.blockv.core.internal.net.rest.api.VatomApiImpl
import io.blockv.core.internal.net.rest.exception.DefaultErrorMapper

/**
 * Created by LordCheddar on 2018/02/21.
 */
class NetModule(val preferences: Preferences, val jsonModule: JsonModule) {

  val client: Client = HttpClient(
    preferences,
    DefaultErrorMapper(),
    jsonModule,
    50000,
    60000)

  val userApi: UserApi = UserApiImpl(client, jsonModule)
  val vatomApi: VatomApi = VatomApiImpl(client, jsonModule)

}