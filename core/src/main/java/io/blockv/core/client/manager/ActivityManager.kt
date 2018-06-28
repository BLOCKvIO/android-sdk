package io.blockv.core.client.manager

import io.blockv.core.model.ActivityMessageList
import io.blockv.core.model.ActivityThreadList
import io.blockv.core.util.Callable

interface ActivityManager {

  fun getThreadList(cursor: String, count: Int): Callable<ActivityThreadList>

  fun getThreadList(cursor: String): Callable<ActivityThreadList>

  fun getThreadList(): Callable<ActivityThreadList>

  fun getThreadMessages(id: String, cursor: String, count: Int): Callable<ActivityMessageList>

  fun getThreadMessages(id: String, cursor: String): Callable<ActivityMessageList>

  fun getThreadMessages(id: String): Callable<ActivityMessageList>

  fun sendMessage(userId: String, message: String): Callable<Void?>

}