package io.blockv.core.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

interface Callable<T> {

  fun runOn(scheduler: Scheduler): Callable<T>

  fun returnOn(responseScheduler: Scheduler): Callable<T>

  fun call(): Cancellable

  fun call(success: ((T) -> Unit)?): Cancellable

  fun call(success: ((T) -> Unit)?, error: ((Throwable) -> Unit)?): Cancellable

  fun call(success: OnSuccess<T>?): Cancellable

  fun call(success: OnSuccess<T>?, error: OnError?): Cancellable

  fun <R> map(map: (T) -> R): Callable<R>

  fun filter(filter: ((T) -> Boolean)?): Callable<T>

  fun doFinally(final: (() -> Unit)?): Callable<T>

  fun doFinally(final: DoFinally?): Callable<T>

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
      val IO_POOL: ThreadPoolExecutor = ThreadPoolExecutor(10, 100, Long.MAX_VALUE, TimeUnit.NANOSECONDS, LinkedBlockingQueue())
      val COMP_POOL: ThreadPoolExecutor = ThreadPoolExecutor(cores * 2, cores * 4, 60L, TimeUnit.SECONDS, LinkedBlockingQueue())

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

        var executionScheduler: Scheduler = Scheduler.IO
        var responseScheduler: Scheduler = Scheduler.MAIN

        private var doFinally: (() -> Unit)? = null

        var flter: ((T) -> Boolean)? = null

        override fun filter(filter: ((T) -> Boolean)?): Callable<T> {
          this.flter = filter
          return this
        }

        override fun <R> map(map: (T) -> R): Callable<R> {
          return create {
            val emitter = it
            val cancel = call({
              emitter.onResult(map.invoke(it))
            }, {
              emitter.onError(it)
            })
            it.doOnCompletion({
              cancel.cancel()
            })
          }
        }

        override fun call(success: OnSuccess<T>?): Cancellable {
          return call(success, null)
        }

        override fun call(success: OnSuccess<T>?, error: OnError?): Cancellable {
          return call({ success?.onSuccess(it) }, { error?.onError(it) })
        }

        override fun doFinally(final: DoFinally?): Callable<T> {
          return doFinally({ final?.doFinally() })
        }

        override fun doFinally(final: (() -> Unit)?): Callable<T> {
          doFinally = final
          return this
        }

        override fun runOn(scheduler: Scheduler): Callable<T> {
          this.executionScheduler = scheduler
          return this
        }

        override fun returnOn(responseScheduler: Scheduler): Callable<T> {
          this.responseScheduler = responseScheduler
          return this
        }

        override fun call(): Cancellable {
          return this.call({})
        }

        override fun call(success: ((T) -> Unit)?): Cancellable {
          return this.call(success, null)
        }

        override fun call(success: ((T) -> Unit)?, error: ((Throwable) -> Unit)?): Cancellable {

          val emitter = object : ResultEmitter<T>, Cancellable {

            private var resp = responseScheduler
            private var onSuccess = success
            private var onError = error
            private val cancelable = CompositeCancellable()
            private var complete: Boolean = false
            private var canceled: Boolean = false
            private var completion: (() -> Unit)? = null
            private var filter: ((T) -> Boolean)? = flter

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
                  resp.execute(Runnable {
                    if (!isCanceled()) {
                      internalSuccess?.invoke(result)
                    }
                  })
                }
              }
            }

            override fun onError(error: Throwable) {
              if (onError != null && !isCanceled()) {
                val internalOnError = onError
                resp.execute(Runnable {
                  if (!isCanceled()) {
                    internalOnError?.invoke(error)
                  }
                })
                onComplete()
              } else
                throw error
            }

            @Synchronized
            override fun onComplete() {
              complete = true
              onSuccess = null
              onError = null
              cancelable.cancel()
              cancelable.clear()
              resp.execute(Runnable {
                doFinally?.invoke()
                completion?.invoke()
                doFinally = null
                completion = null
              })
              filter = null
            }
          }
          val cancel = CompositeCancellable()
          cancel.add(emitter)
          cancel.add(executionScheduler.execute(Runnable {
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

    fun onResult(result: T)

    fun onError(error: Throwable)

    fun onComplete()

    fun isComplete(): Boolean

    fun isCanceled(): Boolean

    fun doOnCompletion(onComplete: (() -> Unit)?)
  }
}