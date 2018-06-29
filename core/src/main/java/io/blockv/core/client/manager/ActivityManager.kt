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
package io.blockv.core.client.manager

import io.blockv.core.model.ActivityMessageList
import io.blockv.core.model.ActivityThreadList
import io.blockv.core.util.Callable

/**
 * This interface contains the available BLOCKv activity functions.
 */
interface ActivityManager {

  /**
   * Fetches a list of the current user's activity threads.
   *
   * This endpoint uses paging, the set of threads to be returned is determined by the
   * cursor and count param.
   *
   * @param cursor allows you to filter out all threads more recent than the cursor.
   *               if omitted or set as zero, the most recent threads are returned.
   *               A new cursor is returned by the server in the ActivityThreadList model.
   * @param count is the number of threads to be returned.
   * @return new Callable<ActivityThreadList> instance
   * @see ActivityThreadList
   */
  fun getThreads(cursor: String, count: Int): Callable<ActivityThreadList>

  /**
   * Fetches a list of the current user's threads.
   *
   * This endpoint uses paging, the set of threads to be returned is determined by the cursor.
   * The count is defaulted to all.
   *
   * @param cursor allows you to filter out all threads more recent than the cursor.
   *               if omitted or set as zero, the most recent threads are returned.
   *               A new cursor is returned by the server in the ActivityThreadList model.
   * @return new Callable<ActivityThreadList> instance.
   * @see ActivityThreadList
   */
  fun getThreads(cursor: String): Callable<ActivityThreadList>

  /**
   * Fetches a list of the current user's threads.
   *
   * This will return a list of all the current user's threads.
   *
   * @return new Callable<ActivityThreadList> instance.
   * @see ActivityThreadList
   */
  fun getThreads(): Callable<ActivityThreadList>

  /**
   * Fetches a list of messages from the specified activity thread.
   *
   * This endpoint uses paging, the set of messages to be returned is determined by the cursor
   * and count param.
   *
   * @param id is the identifier of the thread you want to get messages from.
   * @param cursor allows you to filter out all messages more recent than the cursor.
   *               If omitted, the most recent messages are returned.
   *               A new cursor is returned by the server in the ActivityMessageList model.
   * @param count is the number of messages to be returned.
   * @return new Callable<ActivityMessageList> instance.
   * @see ActivityMessageList
   */
  fun getThreadMessages(id: String, cursor: String, count: Int): Callable<ActivityMessageList>

  /**
   * Fetches a list of messages from the specified activity thread.
   *
   * This endpoint uses paging, the set of messages to be returned is determined by the cursor.
   * The count is defaulted to all.
   *
   * @param id is the identifier of the thread you want to get messages from.
   * @param cursor allows you to filter out all messages more recent than the cursor.
   *               If omitted, the most recent messages are returned.
   *               A new cursor is returned by the server in the ActivityMessageList model.
   * @return new Callable<ActivityMessageList> instance.
   * @see ActivityMessageList
   */
  fun getThreadMessages(id: String, cursor: String): Callable<ActivityMessageList>

  /**
   * Fetches a list of messages from the specified activity thread.
   *
   * This will return all the messages from the specified thread.
   *
   * @param id is the identifier of the thread you want to get messages from.
   * @return new Callable<ActivityMessageList> instance.
   * @see ActivityMessageList
   */
  fun getThreadMessages(id: String): Callable<ActivityMessageList>

  /**
   * Sends a message to the specified BLOCKv user.
   *
   * @param userId is the identifier of the BLOCKv user.
   * @param message is the message you want to send to the other user.
   * @return new Callable<Void> instance
   */
  fun sendMessage(userId: String, message: String): Callable<Void?>

}