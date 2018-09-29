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
package io.blockv.rxcore.client.manager

import io.blockv.common.internal.net.rest.api.ActivityApi
import io.blockv.common.internal.net.rest.request.ActivityMessageListRequest
import io.blockv.common.internal.net.rest.request.ActivityThreadListRequest
import io.blockv.common.internal.net.rest.request.SendMessageRequest
import io.blockv.common.model.ActivityMessageList
import io.blockv.common.model.ActivityThreadList
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ActivityManagerImpl(val api: ActivityApi) : ActivityManager {
  override fun getThreads(cursor: String, count: Int): Single<ActivityThreadList> = Single.fromCallable {
    api.getThreadList(ActivityThreadListRequest(cursor, count)).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getThreads(cursor: String): Single<ActivityThreadList> {
    return getThreads(cursor, 0)
  }

  override fun getThreads(): Single<ActivityThreadList> {
    return getThreads("")
  }

  override fun getThreadMessages(id: String, cursor: String, count: Int): Single<ActivityMessageList> = Single.fromCallable {
    api.getThreadMessages(ActivityMessageListRequest(id, cursor, count)).payload
  }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

  override fun getThreadMessages(id: String, cursor: String): Single<ActivityMessageList> {
    return getThreadMessages(id, cursor, 0)
  }

  override fun getThreadMessages(id: String): Single<ActivityMessageList> {
    return getThreadMessages(id, "")
  }

  override fun sendMessage(userId: String, message: String): Completable = Completable.fromCallable {
    api.sendMessage(SendMessageRequest(userId, message)).payload
  }
}