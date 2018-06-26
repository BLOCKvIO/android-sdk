package io.blockv.core.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

interface Callable<T> {

  fun runOn(scheduler: Scheduler): Callable<T>

  fun returnOn(responseScheduler: Scheduler): Callable<T>

  fun call(): Cancellable

  fun call(success: ((T) -> Unit)?): Cancellable

  fun call(success: ((T) -> Unit)?, error: ((Throwable) -> Unit)?): Cancellable

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

        override fun runOn(scheduler: Scheduler): Callable<T> {
          this.executionScheduler = scheduler
          return this
        }

        override fun returnOn(responseScheduler: Scheduler): Callable<T> {
          this.responseScheduler = responseScheduler
          return this
        }

        override fun call(): Cancellable {
          return this.call(null)
        }

        override fun call(success: ((T) -> Unit)?): Cancellable {
          return this.call(success, null)
        }

        override fun call(success: ((T) -> Unit)?, error: ((Throwable) -> Unit)?): Cancellable {

          val emitter = object : ResultEmitter<T>, Cancellable {

            var resp = responseScheduler
            var onSuccess = success
            var onError = error
            val cancelable = CompositeCancellable()
            var complete: Boolean = false
            var canceled: Boolean = false

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
              cancelable.cancel()
            }

            @Synchronized
            override fun onResult(result: T) {
              if (onSuccess != null && !isComplete() && !isCanceled()) {
                resp.execute(Runnable { onSuccess?.invoke(result) })
              }
            }

            override fun onError(error: Throwable) {
              if (onError != null && !isCanceled()) {
                resp.execute(Runnable { onError?.invoke(error) })
                onError = null
              } else
                throw error
            }

            @Synchronized
            override fun onComplete() {
              complete = true
              onSuccess = null
              onError = null
              cancelable.clear()
            }
          }
          val cancel = CompositeCancellable()
          cancel.add(emitter)
          cancel.add(executionScheduler.execute(Runnable { call(emitter) }))
          return cancel
        }

      }
    }

    fun <T> single(single: () -> T): Callable<T> {
      return create {
        try {
          it.onResult(single())
          it.onComplete()
        } catch (e: Exception) {
          it.onError(e)
        }
      }
    }
  }

  interface ResultEmitter<T> {

    fun onResult(result: T)

    fun onError(error: Throwable)

    fun onComplete()
  }
}