package io.blockv.core.client.manager

import io.blockv.common.internal.net.rest.api.AppApi
import io.blockv.common.internal.net.rest.request.PushTokenRequest
import io.blockv.common.model.AppVersion
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class AppManagerImpl(val api: AppApi) : AppManager {

  override fun getSupportedVersion(): Single<AppVersion> {
    return Single.fromCallable {
      api.getAppVersion()
        .payload
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun registerPushToken(token: String): Single<JSONObject> {
    return Single.fromCallable {
      api.registerPushToken(PushTokenRequest(token, true))
        .payload
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun unRegisterPushToken(token: String): Single<JSONObject> {
    return Single.fromCallable {
      api.registerPushToken(PushTokenRequest(token, false))
        .payload
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }
}