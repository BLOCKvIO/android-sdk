package io.blockv.android.core.internal.net

import io.blockv.android.core.internal.net.rest.Client
import io.blockv.android.core.internal.net.rest.HttpClient
import io.blockv.core.internal.net.rest.api.UserApi
import io.blockv.core.internal.net.rest.api.UserApiImpl
import io.blockv.core.internal.net.rest.exception.DefaultErrorMapper
import io.blockv.core.internal.json.JsonModule
import io.blockv.android.core.internal.repository.Preferences

/**
 * Created by LordCheddar on 2018/02/21.
 */
class NetModule(val preferences: Preferences,val jsonModule: JsonModule) {

  val client: Client = HttpClient(
    preferences,
    DefaultErrorMapper(),
    50000,
    60000)

  val userApi: UserApi = UserApiImpl(client,jsonModule)

}