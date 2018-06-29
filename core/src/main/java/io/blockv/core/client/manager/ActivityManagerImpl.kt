package io.blockv.core.client.manager

import io.blockv.core.internal.net.rest.api.ActivityApi
import io.blockv.core.internal.net.rest.request.ActivityMessageListRequest
import io.blockv.core.internal.net.rest.request.ActivityThreadListRequest
import io.blockv.core.internal.net.rest.request.SendMessageRequest
import io.blockv.core.model.ActivityMessageList
import io.blockv.core.model.ActivityThreadList
import io.blockv.core.util.Callable

class ActivityManagerImpl(val api: ActivityApi) : ActivityManager {

  override fun getThreads(cursor: String, count: Int): Callable<ActivityThreadList> = Callable.single {
    api.getThreadList(ActivityThreadListRequest(cursor, count)).payload
  }
    .runOn(Callable.Scheduler.IO)
    .returnOn(Callable.Scheduler.MAIN)

  override fun getThreads(cursor: String): Callable<ActivityThreadList> {
    return getThreads(cursor, 0)
  }

  override fun getThreads(): Callable<ActivityThreadList> {
    return getThreads("")
  }

  override fun getThreadMessages(id: String, cursor: String, count: Int): Callable<ActivityMessageList> = Callable.single {
    api.getThreadMessages(ActivityMessageListRequest(id, cursor, count)).payload
  }
    .runOn(Callable.Scheduler.IO)
    .returnOn(Callable.Scheduler.MAIN)

  override fun getThreadMessages(id: String, cursor: String): Callable<ActivityMessageList> {
    return getThreadMessages(id, cursor, 0)
  }

  override fun getThreadMessages(id: String): Callable<ActivityMessageList> {
    return getThreadMessages(id, "")
  }

  override fun sendMessage(userId: String, message: String): Callable<Void?> = Callable.single {
    api.sendMessage(SendMessageRequest(userId, message)).payload
  }
}