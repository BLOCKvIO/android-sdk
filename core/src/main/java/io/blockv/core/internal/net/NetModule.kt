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
package io.blockv.core.internal.net

import io.blockv.core.internal.json.JsonModule
import io.blockv.core.internal.net.rest.Client
import io.blockv.core.internal.net.rest.HttpClient
import io.blockv.core.internal.net.rest.api.*
import io.blockv.core.internal.net.rest.auth.Authenticator
import io.blockv.core.internal.net.rest.exception.DefaultErrorMapper
import io.blockv.core.internal.repository.Preferences


class NetModule(val authenticator: Authenticator, val preferences: Preferences, val jsonModule: JsonModule) {

  val client: Client = HttpClient(
    preferences,
    DefaultErrorMapper(),
    jsonModule,
    authenticator,
    50000,
    60000)

  val userApi: UserApi = UserApiImpl(client, jsonModule)
  val vatomApi: VatomApi = VatomApiImpl(client, jsonModule)
  val activityApi: ActivityApi = ActivityApiImpl(client, jsonModule)
}