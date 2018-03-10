package io.blockv.core.util

import android.os.AsyncTask
import io.blockv.core.util.Disposable


/**
 * Created by LordCheddar on 2018/02/21.
 */
abstract class Observable<out T> {

  @Throws(Exception::class)
  abstract fun getResult(): T?

  fun subscribe(): Disposable {
    return this.subscribe(null)
  }
  fun subscribe(success: OnSuccess<T>?): Disposable {
    return this.subscribe(success, null)
  }

  fun subscribe(success: OnSuccess<T>?, error: OnError?): Disposable {

    val asynk: AsyncTask<Void, Void, Any> = object : AsyncTask<Void, Void, Any>() {

      override fun doInBackground(vararg params: Void?): Any? {
        try {
          val result = getResult() ?: return null
          return result as Any
        } catch (e: Exception) {
          return e
        }
      }

      override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        when (result) {
          null -> success?.onSuccess(null)
          is Exception -> error?.onError(result)
          else -> success?.onSuccess(result as T)
        }
      }

    }

    asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    return object : Disposable {
      override fun isDisposed(): Boolean {
        return asynk.isCancelled or (asynk.status == AsyncTask.Status.FINISHED)
      }

      override fun dispose() {
        asynk.cancel(true)
      }

    }

  }

  interface OnSuccess<in T> {
    fun onSuccess(response: T?)
  }

  interface OnError {
    fun onError(response: Throwable)
  }

  /*fun toCompletable(): Completable {
    val callable = this
    return object : Completable() {
      override fun subscribe(complete: OnComplete, error: OnError): Disposable {
        callable.subscribe(object : OnSuccess<T> {
          override fun onSuccess(response: T) {
            complete.onComplete()
          }
        }, object : Observable.OnError {
          override fun onError(response: Throwable) {
            error.onError(response)
          }
        })
      }
    }
  }*/


}