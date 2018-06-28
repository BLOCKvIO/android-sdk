package io.blockv.rx.client.manager

import io.blockv.core.model.ActivityMessageList
import io.blockv.core.model.ActivityThreadList
import io.reactivex.Completable
import io.reactivex.Single

interface ActivityManager {

  fun getThreadList(cursor: String, count: Int): Single<ActivityThreadList>

  fun getThreadList(cursor: String): Single<ActivityThreadList>

  fun getThreadList(): Single<ActivityThreadList>

  fun getThreadMessages(id: String, cursor: String, count: Int): Single<ActivityMessageList>

  fun getThreadMessages(id: String, cursor: String): Single<ActivityMessageList>

  fun getThreadMessages(id: String): Single<ActivityMessageList>

  fun sendMessage(userId: String, message: String): Completable

}