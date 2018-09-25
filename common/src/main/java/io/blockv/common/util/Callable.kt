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
package io.blockv.common.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.*


interface Callable<T> {

  fun runOn(scheduler: Scheduler): Callable<T>

  fun returnOn(responseScheduler: Scheduler): Callable<T>

  fun call(): Cancellable

  fun call(success: ((T) -> Unit)?): Cancellable

  fun call(success: ((T) -> Unit)?, error: ((Throwable) -> Unit)?): Cancellable

  fun call(success: OnSuccess<T>?): Cancellable

  fun call(success: OnSuccess<T>?, error: OnError?): Cancellable

  fun <R> map(map: (T) -> R): Callable<R>

  fun <R> flatMap(map: (T) -> Callable<R>): Callable<R>

  fun first(): T?

  fun filter(filter: ((T) -> Boolean)?): Callable<T>

  fun doFinally(final: (() -> Unit)?): Callable<T>

  fun doFinally(final: DoFinally?): Callable<T>

  fun doOnError(onError: ((Throwable) -> Unit)?): Callable<T>

  fun doOnError(onError: OnError?): Callable<T>

  enum class Scheduler {
    IO {
      override fun execute(runnable: java.lang.Runnable): Cancellable {
        val run = Scheduler.wrapRunnable(runnable, { Scheduler.IO_POOL.remove(it) })
        Scheduler.IO_POOL.execute(run)
        return run
      }
    },
    COMP {
      override fun execute(runnable: java.lang.Runnable): Cancellable {
        val run = Scheduler.wrapRunnable(runnable, { Scheduler.COMP_POOL.remove(it) })
        Scheduler.COMP_POOL.execute(run)
        return run
      }
    },
    MAIN {
      override fun execute(runnable: java.lang.Runnable): Cancellable {
        val handler = Handler(Looper.getMainLooper())
        val run = Scheduler.wrapRunnable(runnable, { handler.removeCallbacks(it) })
        handler.post(run)
        return run
      }
    };

    abstract fun execute(runnable: java.lang.Runnable): Cancellable

    companion object {
      private val cores = Runtime.getRuntime().availableProcessors()
      val IO_POOL: ThreadPoolExecutor =
        ThreadPoolExecutor(10, 100, Long.MAX_VALUE, TimeUnit.NANOSECONDS, LinkedBlockingQueue())
      val COMP_POOL: ThreadPoolExecutor =
        ThreadPoolExecutor(cores * 2, cores * 4, 60L, TimeUnit.SECONDS, LinkedBlockingQueue())

      fun wrapRunnable(runnable: java.lang.Runnable, onCancel: (java.lang.Runnable) -> Unit): Runnable {

        return object : Runnable() {
          override fun onCancel(): Boolean {
            onCancel.invoke(this)
            return true
          }

          override fun onRun() {
            runnable.run()
          }
        }
      }
    }

    abstract class Runnable : java.lang.Runnable, Cancellable {
      private var completed: Boolean = false
      private var canceled: Boolean = false

      override fun isComplete(): Boolean {
        return completed
      }

      override fun isCanceled(): Boolean {
        return canceled
      }

      override fun cancel() {
        if (!completed && !canceled) {
          canceled = onCancel()
        }
      }

      abstract fun onCancel(): Boolean

      abstract fun onRun()

      override fun run() {
        onRun()
        completed = true
      }

    }

  }

  companion object {

    fun <T> create(call: (ResultEmitter<T>) -> Unit): Callable<T> {
      return object : Callable<T> {

        val concurrent = 10
        val lock = Semaphore(concurrent)
        var execScheduler: Scheduler = Scheduler.IO
        var respScheduler: Scheduler = Scheduler.MAIN

        private var doFinally: (() -> Unit)? = null

        private var doOnError: ((Throwable) -> Unit)? = null

        var flter: ((T) -> Boolean)? = null

        override fun filter(filter: ((T) -> Boolean)?): Callable<T> {
          this.flter = filter
          return this
        }

        override fun <R> map(map: (T) -> R): Callable<R> {
          return create<R> { emitter ->
            val cancel = call({ result ->
              try {
                emitter.onResult(map.invoke(result))
              } catch (e: Exception) {
                emitter.onError(e)
              }
            },
              { throwable ->
                emitter.onError(throwable)
              })

            emitter.doOnCompletion {
              cancel.cancel()
            }

            val oldDoFinally = doFinally
            doFinally {
              oldDoFinally?.invoke()
              emitter.onComplete()
            }
          }.runOn(respScheduler)

        }

        override fun <R> flatMap(map: (T) -> Callable<R>): Callable<R> {

          return create<R> { emitter ->
            var complete = false
            val cancel = CompositeCancellable()

            cancel.add(call({ result ->

              synchronized(cancel) {
                val callable =
                  map.invoke(result)
                    .runOn(emitter.executionScheduler)
                    .returnOn(emitter.responseScheduler)
                    .doFinally {
                      synchronized(cancel) {
                        if (complete && cancel.isComplete()) {
                          emitter.onComplete()
                        }
                      }
                    }

                cancel.add(callable.call(
                  object : OnSuccess<R> {
                    override fun onSuccess(success: R) {
                      emitter.onResult(success)
                    }
                  },
                  object : OnError {
                    override fun onError(error: Throwable) {
                      emitter.onError(error)
                    }
                  }
                ))
              }

            }, { throwable ->
              emitter.onError(throwable)
            }))

            emitter.doOnCompletion {
              cancel.cancel()
            }

            doFinally {
              synchronized(cancel) {
                complete = true //top stream is complete
                if (cancel.isComplete()) {
                  emitter.onComplete()
                }
              }
            }
          }.runOn(respScheduler)
        }

        override fun first(): T? {
          val latch = CountDownLatch(1)
          var value: T? = null
          var throwable: Throwable? = null
          val cancel = CompositeCancellable()
          cancel.add(call({
            value = it
            cancel.cancel()
            latch.countDown()
          }, {
            throwable = it
            latch.countDown()
          }))
          latch.await()

          if (throwable != null) throw throwable!!

          return value
        }

        override fun call(success: OnSuccess<T>?): Cancellable {
          return call(success, null)
        }

        override fun call(success: OnSuccess<T>?, error: OnError?): Cancellable {
          return call({ success?.onSuccess(it) }, { error?.onError(it) })
        }

        override fun doFinally(final: DoFinally?): Callable<T> {
          return doFinally { final?.doFinally() }
        }

        override fun doFinally(final: (() -> Unit)?): Callable<T> {
          doFinally = final
          return this
        }

        override fun doOnError(onError: OnError?): Callable<T> {
          return doOnError { onError?.onError(it) }
        }

        override fun doOnError(onError: ((Throwable) -> Unit)?): Callable<T> {
          this.doOnError = onError
          return this
        }

        override fun runOn(scheduler: Scheduler): Callable<T> {
          this.execScheduler = scheduler
          return this
        }

        override fun returnOn(responseScheduler: Scheduler): Callable<T> {
          this.respScheduler = responseScheduler
          return this
        }

        override fun call(): Cancellable {
          return this.call {}
        }

        override fun call(success: ((T) -> Unit)?): Cancellable {
          return this.call(success, null)
        }

        override fun call(success: ((T) -> Unit)?, error: ((Throwable) -> Unit)?): Cancellable {

          val emitter = object : ResultEmitter<T>, Cancellable {

            private var resp = respScheduler
            private var exec = execScheduler
            private var onSuccess = success
            private var onError = error
            private val cancelable = CompositeCancellable()
            private var complete: Boolean = false
            private var canceled: Boolean = false
            private var completion: (() -> Unit)? = null
            private var filter: ((T) -> Boolean)? = flter

            override val executionScheduler: Scheduler
              get() = resp
            override val responseScheduler: Scheduler
              get() = exec


            override fun doOnCompletion(onComplete: (() -> Unit)?) {
              completion = onComplete
            }

            @Synchronized
            override fun isComplete(): Boolean {
              return complete
            }

            @Synchronized
            override fun isCanceled(): Boolean {
              return canceled
            }

            @Synchronized
            override fun cancel() {
              canceled = true
              onComplete()
            }

            @Synchronized
            override fun onResult(result: T) {
              if (onSuccess != null && !isComplete() && !isCanceled()) {
                if (filter == null || filter!!.invoke(result)) {
                  val internalSuccess = onSuccess
                  lock.acquire()
                  resp.execute(Runnable {
                    if (!isCanceled()) {
                      internalSuccess?.invoke(result)
                    }
                    lock.release()
                  })
                }
              }
            }

            override fun onError(error: Throwable) {
              if (onError != null && !isCanceled() && !isComplete()) {
                val internalOnError = onError
                val internalDoOnError = doOnError
                resp.execute(Runnable {
                  if (!isCanceled()) {
                    internalOnError?.invoke(error)
                    internalDoOnError?.invoke(error)
                  }
                  onComplete()
                })

              } else
                if (!isCanceled() && !isComplete()) {
                  throw error
                }
            }

            @Synchronized
            override fun onComplete() {

              resp.execute(Runnable {
                lock.acquire(concurrent)
                complete = true
                doFinally?.invoke()
                completion?.invoke()
                onSuccess = null
                onError = null
                cancelable.cancel()
                cancelable.clear()
                doFinally = null
                completion = null
                filter = null
                lock.release(concurrent)
              })

            }
          }
          val cancel = CompositeCancellable()
          cancel.add(emitter)
          cancel.add(execScheduler.execute(Runnable {
            call(emitter)
          }))
          return cancel
        }

      }
    }

    fun <T> single(result: () -> T): Callable<T> {
      return create {
        try {
          it.onResult(result.invoke())
          it.onComplete()
        } catch (e: Exception) {
          it.onError(e)
        }
      }
    }

    fun <T> singleResult(result: OnSingleResult<T>): Callable<T> {
      return single { result.onResult() }
    }
  }

  @FunctionalInterface
  interface OnSingleResult<T> {
    @Throws(Exception::class)
    fun onResult(): T
  }

  @FunctionalInterface
  interface OnSuccess<T> {
    fun onSuccess(success: T)
  }

  @FunctionalInterface
  interface OnError {
    fun onError(error: Throwable)
  }

  @FunctionalInterface
  interface DoFinally {
    fun doFinally()
  }

  interface ResultEmitter<T> {

    val executionScheduler: Scheduler

    val responseScheduler: Scheduler

    fun onResult(result: T)

    fun onError(error: Throwable)

    fun onComplete()

    fun isComplete(): Boolean

    fun isCanceled(): Boolean

    fun doOnCompletion(onComplete: (() -> Unit)?)
  }
}
