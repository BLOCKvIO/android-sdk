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

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

abstract class Callable<out T> {

  companion object {
    private val cores = Runtime.getRuntime().availableProcessors()
    val ioPool: ThreadPoolExecutor = ThreadPoolExecutor(10, 100, Long.MAX_VALUE, TimeUnit.NANOSECONDS, LinkedBlockingQueue())
    val compPool: ThreadPoolExecutor = ThreadPoolExecutor(cores * 2, cores * 4, 60L, TimeUnit.SECONDS, LinkedBlockingQueue())
  }

  enum class Scheduler {
    IO,
    COMP,
    MAIN
  }

  var executionScheduler: Scheduler = Scheduler.IO
  var responseScheduler: Scheduler = Scheduler.MAIN

  fun runOn(scheduler: Scheduler) {
    this.executionScheduler = scheduler
  }

  fun returnOn(responseScheduler: Scheduler) {
    this.responseScheduler = responseScheduler
  }

  @Throws(Exception::class)
  abstract fun getResult(): T?

  fun call(): Cancellable {
    return this.call(null)
  }

  fun call(success: OnSuccess<T>?): Cancellable {
    return this.call(success, null)
  }

  fun call(success: OnSuccess<T>?, error: OnError?): Cancellable {

    val runnable: Runnable = object : Runnable() {
      override fun run() {
        try {
          val result = getResult()
          this.completed = true
          if (success != null && !canceled) {
            when (responseScheduler) {
              Scheduler.IO -> {
                ioPool.execute({ success.onSuccess(result) })
              }
              Scheduler.COMP -> {
                compPool.execute({ success.onSuccess(result) })
              }
              Scheduler.MAIN -> {
                Handler(Looper.getMainLooper()).post({ success.onSuccess(result) })
              }
            }
          }
        } catch (e: Exception) {
          this.completed = true
          if (error != null && !canceled) {
            when (responseScheduler) {
              Scheduler.IO -> {
                ioPool.execute({ error.onError(e) })
              }
              Scheduler.COMP -> {
                compPool.execute({ error.onError(e) })
              }
              Scheduler.MAIN -> {
                Handler(Looper.getMainLooper()).post({ error.onError(e) })
              }
            }

          } else
            throw e
        }
      }
    }

    when (executionScheduler) {
      Scheduler.IO -> {
        ioPool.execute(runnable)
        return object : Cancellable(runnable) {
          override fun cancel() {
            runnable.canceled = true
            ioPool.remove(runnable)
          }
        }
      }
      Scheduler.COMP -> {
        compPool.execute(runnable)
        return object : Cancellable(runnable) {
          override fun cancel() {
            runnable.canceled = true
            compPool.remove(runnable)
          }
        }
      }
      Scheduler.MAIN -> {
        val handler = Handler(Looper.getMainLooper())
        handler.post(runnable)
        return object : Cancellable(runnable) {
          override fun cancel() {
            runnable.canceled = true
            handler.removeCallbacks(runnable)
          }
        }
      }

    }

  }

  abstract class Cancellable(private val runnable: Runnable) : io.blockv.core.util.Cancellable {
    override fun isComplete(): Boolean {
      return runnable.isComplete
    }

    override fun isCanceled(): Boolean {
      return runnable.isCanceled
    }
  }

  abstract class Runnable : java.lang.Runnable {
    internal var completed: Boolean = false
    internal var canceled: Boolean = false
    val isComplete: Boolean
      get() {
        return completed
      }
    val isCanceled: Boolean
      get() {
        return canceled
      }

  }

  interface OnSuccess<in T> {
    fun onSuccess(response: T?)
  }

  interface OnError {
    fun onError(response: Throwable)
  }

}