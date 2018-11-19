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
import io.blockv.common.util.Callable.Scheduler.Companion.INTERNAL_POOL
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

  fun doOnSuccess(onSuccess: (() -> Unit)?): Callable<T>

  fun doOnSuccess(onSuccess: OnSuccessful?): Callable<T>

  fun doOnCancel(onCancel: (() -> Unit)?): Callable<T>

  fun doOnCancel(onCancel: OnCancel?): Callable<T>

  fun doOnResult(onResult: ((T) -> Unit)?): Callable<T>

  fun doOnResult(onResult: OnResult<T>?): Callable<T>


  enum class Scheduler {
    IO {
      override fun execute(runnable: java.lang.Runnable): Cancellable {
        val run = Scheduler.wrapRunnable(runnable) { Scheduler.IO_POOL.remove(it) }
        Scheduler.IO_POOL.execute(run)
        return run
      }
    },
    COMP {
      override fun execute(runnable: java.lang.Runnable): Cancellable {
        val run = Scheduler.wrapRunnable(runnable) { Scheduler.COMP_POOL.remove(it) }
        Scheduler.COMP_POOL.execute(run)
        return run
      }
    },
    MAIN {
      override fun execute(runnable: java.lang.Runnable): Cancellable {
        val handler = Handler(Looper.getMainLooper())
        val run = Scheduler.wrapRunnable(runnable) { handler.removeCallbacks(it) }
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
        ThreadPoolExecutor(cores * 2, 100, Long.MAX_VALUE, TimeUnit.NANOSECONDS, LinkedBlockingQueue())
      val INTERNAL_POOL: ThreadPoolExecutor =
        ThreadPoolExecutor(10, 100, Long.MAX_VALUE, TimeUnit.NANOSECONDS, LinkedBlockingQueue())


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
        val completeLock = Semaphore(1)
        var execScheduler: Scheduler = Scheduler.IO
        var respScheduler: Scheduler = Scheduler.MAIN

        private var doFinally: (() -> Unit)? = null

        private var doOnError: ((Throwable) -> Unit)? = null

        private var doOnCancel: (() -> Unit)? = null

        private var doOnResult: ((T) -> Unit)? = null

        private var doOnSuccess: (() -> Unit)? = null

        var flter: ((T) -> Boolean)? = null

        override fun filter(filter: ((T) -> Boolean)?): Callable<T> {
          this.flter = filter
          return this
        }

        override fun <R> map(map: (T) -> R): Callable<R> {
          return create<R> { emitter ->

            val cancel = CompositeCancellable()

            emitter.completion = object : ResultEmitter.Completion {
              override fun onComplete() {
                cancel.cancel()
              }
            }

            val oldDoOnSuccess = doOnSuccess
            doOnSuccess {
              emitter.onComplete()
              oldDoOnSuccess?.invoke()
            }

            val oldDoOnCancel = doOnCancel
            doOnCancel {
              cancel.cancel()
              oldDoOnCancel?.invoke()
            }

            cancel.add(
              call({ result ->
                try {
                  emitter.onResult(map.invoke(result))
                } catch (e: Exception) {
                  emitter.onError(e)
                }
              },
                { throwable ->
                  emitter.onError(throwable)
                })
            )

          }.runOn(respScheduler)

        }

        override fun <R> flatMap(map: (T) -> Callable<R>): Callable<R> {

          return create<R> { emitter ->
            var complete = false
            val cancel = CompositeCancellable()
            var count = 0

            val oldDoOnSuccess = doOnSuccess

            doOnSuccess {

              synchronized(cancel) {
                complete = true

                if (count == 0) {
                  emitter.onComplete()
                }
              }
              oldDoOnSuccess?.invoke()

            }

            val oldDoOnCancel = doOnCancel
            doOnCancel {
              cancel.cancel()
              oldDoOnCancel?.invoke()
            }

            emitter.completion = object : ResultEmitter.Completion {
              override fun onComplete() {
                if (emitter.isCanceled()) {
                  cancel.cancel()
                }
              }
            }


            cancel.add(call({ result ->

              //
              synchronized(cancel) {
                count++
                val callable =
                  map.invoke(result)
                    .runOn(emitter.executionScheduler)
                    .returnOn(emitter.responseScheduler)
                    .doOnSuccess {
                      synchronized(cancel) {
                        count--
                        if (complete && count == 0) {
                          // System.out.println("complete")
                          emitter.onComplete()
                        }
                      }
                    }

                cancel.add(callable.call({
                  emitter.onResult(it)
                }, {
                  emitter.onError(it)
                  cancel.cancel()
                }))
              }

            }, { throwable ->
              emitter.onError(throwable)
            }))

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

        override fun doOnSuccess(onSuccess: (() -> Unit)?): Callable<T> {
          this.doOnSuccess = onSuccess
          return this
        }

        override fun doOnSuccess(onSuccess: OnSuccessful?): Callable<T> {
          return doOnSuccess {
            onSuccess?.onSuccess()
          }
        }

        override fun doOnCancel(onCancel: (() -> Unit)?): Callable<T> {
          this.doOnCancel = onCancel
          return this
        }

        override fun doOnCancel(onCancel: OnCancel?): Callable<T> {
          return doOnCancel {
            onCancel?.onCancel()
          }
        }

        override fun doOnResult(onResult: ((T) -> Unit)?): Callable<T> {
          this.doOnResult = onResult
          return this
        }

        override fun doOnResult(onResult: OnResult<T>?): Callable<T> {
          return doOnResult {
            onResult?.onResult(it)
          }
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
            @set:Synchronized
            @get:Synchronized
            private var complete: Boolean = false
            @set:Synchronized
            @get:Synchronized
            private var canceled: Boolean = false
            @set:Synchronized
            @get:Synchronized
            private var errored: Boolean = false
            @set:Synchronized
            @get:Synchronized
            private var finalized: Boolean = false

            private var completionHandler: ResultEmitter.Completion? = null
            private var filter: ((T) -> Boolean)? = flter

            override var completion: ResultEmitter.Completion?
              get() = completionHandler
              set(value) {
                completionHandler = value
              }

            override val executionScheduler: Scheduler
              get() = resp
            override val responseScheduler: Scheduler
              get() = exec

            override fun doOnCompletion(onComplete: (() -> Unit)?) {
              completion = if (onComplete != null) object : ResultEmitter.Completion {
                override fun onComplete() {
                  onComplete.invoke()
                }
              }
              else null
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
              if (!isComplete()) {
                val internalCancel = doOnCancel
                resp.execute(Runnable {
                  internalCancel?.invoke()
                })
              }
              finalize()
            }

            @Synchronized
            override fun onResult(result: T) {
              if (onSuccess != null && !isComplete() && !isCanceled()) {
                if (filter == null || filter!!.invoke(result)) {
                  val internalSuccess = onSuccess
                  val internalResult = doOnResult

                  val acquired = lock.tryAcquire()
                  resp.execute(Runnable {
                    if (!isCanceled()) {
                      internalResult?.invoke(result)
                      internalSuccess?.invoke(result)
                    }
                    if (acquired) lock.release()
                  })

                }
              }
            }

            override fun onError(error: Throwable) {
              this.errored = true
              val internalError = doOnError

              if (onError != null && !isCanceled() && !isComplete()) {
                val internalOnError = onError
                val internalDoOnError = doOnError
                resp.execute(Runnable {
                  if (!isCanceled()) {
                    internalError?.invoke(error)
                    internalOnError?.invoke(error)
                    internalDoOnError?.invoke(error)
                  }
                  finalize()
                })

              } else
                if (!isCanceled() && !isComplete()) {
                  internalError?.invoke(error)
                  finalize()
                  throw error
                }
            }

            @Synchronized
            fun finalize() {
              if (!finalized) {
                finalized = true
                INTERNAL_POOL.execute {
                  synchronized(completeLock)
                  {
                    completeLock.acquire()
                    lock.acquire(concurrent)
                    complete = true

                    val tempDoFinal = doFinally
                    val tempComplete = completion
                    resp.execute(Runnable {
                      tempDoFinal?.invoke()
                      tempComplete?.onComplete()
                    })

                    onSuccess = null
                    onError = null
                    cancelable.cancel()
                    // cancelable.clear()
                    doFinally = null
                    doOnResult = null
                    doOnSuccess = null
                    doOnCancel = null
                    completion = null
                    filter = null
                    lock.release(concurrent)
                    completeLock.release()
                  }
                }
              }
            }

            @Synchronized
            override fun onComplete() {
              if (!isCanceled() && !errored && !isComplete()) {
                val temp = doOnSuccess
                INTERNAL_POOL.execute {
                  synchronized(completeLock)
                  {
                    completeLock.acquire()
                    lock.acquire(concurrent)
                    complete = true

                    resp.execute(Runnable {
                      temp?.invoke()
                      lock.release(concurrent)
                      completeLock.release()
                    })

                    finalize()
                  }
                }

              }
            }
          }

          val cancel = CompositeCancellable()
          cancel.add(emitter)
          cancel.add(
            execScheduler.execute(Runnable
            {
              synchronized(cancel)
              {
                if (!cancel.isCanceled()) {
                  call(emitter)
                }
              }
            })
          )

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

  @FunctionalInterface
  interface OnSuccessful {
    fun onSuccess()
  }

  @FunctionalInterface
  interface OnCancel {
    fun onCancel()
  }

  @FunctionalInterface
  interface OnResult<T> {
    fun onResult(result: T)
  }


  interface ResultEmitter<T> {

    var completion: Completion?

    val executionScheduler: Scheduler

    val responseScheduler: Scheduler

    fun onResult(result: T)

    fun onError(error: Throwable)

    fun onComplete()

    fun isComplete(): Boolean

    fun isCanceled(): Boolean

    @Deprecated("Set completion instead")
    fun doOnCompletion(onComplete: (() -> Unit)?)

    interface Completion {
      fun onComplete()
    }
  }

}
