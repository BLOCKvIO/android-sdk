package io.blockv.face.client

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