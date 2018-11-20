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
package io.blockv.face.client.manager

import io.blockv.common.model.PublicUser
import io.blockv.common.util.Callable

interface UserManager {

  /**
   * Fetches the publicly available attributes of any user given their user id.
   *
   * Since users are given control over which attributes they make public, you should make
   * provision for receiving all, some, or none of their public attributes.
   *
   * @param userId is the unique identifier of the user.
   * @return new Callable<PublicUser>.
   * @see PublicUser
   */
  fun getPublicUser(userId: String): Callable<PublicUser?>

  /**
   * Fetches the current user's public information from the BLOCKv platform.
   *
   * @return new Callable<PublicUser> instance.
   */
  fun getCurrentUser(): Callable<PublicUser?>

}