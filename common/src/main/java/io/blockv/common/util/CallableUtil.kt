package io.blockv.common.util

import java.util.*

class CallableUtil {

  companion object {

    fun <T> retry(
      callable: Callable<T>,
      retry: (retryCount: Int, throwable: Throwable) -> Boolean
    ): Callable<Value<T>> {

      return Callable.create<Value<T>> { emitter ->

        val cancellable = CompositeCancellable()

        var retryEmitter: Callable.ResultEmitter<Int>? = null

        cancellable.add(Callable.create<Int> {
          retryEmitter = it
          it.onResult(0)
        }
          .runOn(Callable.Scheduler.COMP)
          .returnOn(Callable.Scheduler.COMP)
          .call({

            cancellable.add(
              callable
                .doOnSuccess {
                  emitter.onComplete()
                }
                .call({ value ->
                  emitter.onResult(Value(value, null))
                }, { throwable ->
                  if (retry.invoke(it, throwable)) {
                    retryEmitter?.onResult(1 + it)
                  } else {
                    retryEmitter?.onError(throwable)
                  }
                })
            )

          }, {
            emitter.onError(it)
          }))

        emitter.completion = object : Callable.ResultEmitter.Completion {
          override fun onComplete() {
            cancellable.cancel()
          }
        }
      }
        .runOn(Callable.Scheduler.COMP)
        .returnOn(Callable.Scheduler.COMP)
    }

    fun <T, R> zipFirst(callables: List<Callable<T>>, combine: (values: List<T?>) -> R): Callable<R> {

      return Callable.create { emitter ->

        val out = HashMap<Callable<T>, T>()

        var count = 0
        val cancel = CompositeCancellable()
        callables.forEach { callable ->
          val cancellable = CompositeCancellable()

          cancellable.add(callable.call(
            {
              synchronized(callable)
              {
                synchronized(out)
                {
                  if (!out.containsKey(callable)) {
                    out[callable] = it
                    count++
                    if (count >= callables.size) {
                      val list = ArrayList<T?>()
                      out.keys.forEach { key ->
                        list.add(out[key])
                      }
                      emitter.onResult(combine.invoke(list))
                      emitter.onComplete()
                    }
                  }
                }
              }
              cancellable.cancel()
            }, {
              emitter.onError(it)
            })
          )
          cancel.add(cancellable)
        }
      }
    }

    fun timer(time: Long): Callable<Void?> {

      return Callable.create { emitter ->
        val timer = Timer()
        timer.schedule(object : TimerTask() {
          override fun run() {
            emitter.onResult(null)
            emitter.onComplete()
          }
        }, time)
        emitter.doOnCompletion { timer.cancel() }
      }
    }
  }


  class Value<T>(val value: T?, val throwable: Throwable?)
}