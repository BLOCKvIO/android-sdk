/**
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.core.util

import android.os.AsyncTask

abstract class Callable<out T> {

  @Throws(Exception::class)
  abstract fun getResult(): T?

  fun call(): Cancellable {
    return this.call(null)
  }

  fun call(success: OnSuccess<T>?): Cancellable {
    return this.call(success, null)
  }

  fun call(success: OnSuccess<T>?, error: OnError?): Cancellable {

    val result: OnResult<T> = object : OnResult<T> {
      override fun onResult(): T? {
        return getResult()
      }
    }
    val async: AsyncTask<Void, Void, Any> = CallableTask(result, success, error)

    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    return object : Cancellable {

      override fun isComplete(): Boolean {
        return async.status == AsyncTask.Status.FINISHED
      }

      override fun isCanceled(): Boolean {
        return async.isCancelled
      }

      override fun cancel() {
        async.cancel(true)
      }

    }

  }

  interface OnSuccess<in T> {
    fun onSuccess(response: T?)
  }

  interface OnError {
    fun onError(response: Throwable)
  }

  interface OnResult<out T> {
    fun onResult(): T?
  }

  class CallableTask<T>(private val onResult: OnResult<T>,
                        private val success: OnSuccess<T>?,
                        private val error: OnError?) : AsyncTask<Void, Void, Any>() {

    override fun doInBackground(vararg params: Void?): Any? {
      try {
        val result = onResult.onResult() ?: return null
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
}