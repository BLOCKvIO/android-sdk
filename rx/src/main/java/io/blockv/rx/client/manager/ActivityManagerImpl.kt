package io.blockv.rx.client.manager

import io.blockv.core.internal.net.rest.api.ActivityApi
import io.blockv.core.internal.net.rest.request.ActivityMessageListRequest
import io.blockv.core.internal.net.rest.request.ActivityThreadListRequest
import io.blockv.core.internal.net.rest.request.SendMessageRequest
import io.blockv.core.model.ActivityMessageList
import io.blockv.core.model.ActivityThreadList
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