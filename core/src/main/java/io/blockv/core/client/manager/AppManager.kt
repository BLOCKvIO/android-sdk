package io.blockv.core.client.manager

import io.blockv.common.model.AppVersion
import io.reactivex.Single
import org.json.JSONObject

interface AppManager {

  /**
   * Fetches the current supported app version.
   *
   * @return new Single<AppVersion> instance.
   */
  fun getSupportedVersion(): Single<AppVersion>

  /**
   * Register a FCM token to receive push notifications.
   *
   * @param token is the Firebase cloud messaging token from the device.
   *
   * @return new Single<JSONObject> instance.
   */
  fun registerPushToken(token: String): Single<JSONObject>

  /**
   * Unregister a FCM token to disable push notifications.
   *
   * @param token is the Firebase cloud messaging token from the device.
   *
   * @return new Single<JSONObject> instance.
   */
  fun unRegisterPushToken(token: String): Single<JSONObject>
}