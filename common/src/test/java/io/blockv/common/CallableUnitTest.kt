package io.blockv.common

import io.blockv.common.util.Callable
import org.awaitility.Awaitility
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import repeat.Repeat
import repeat.RepeatRule
import java.util.*
import java.util.concurrent.TimeUnit


class CallableUnitTest {

  @Rule
  @JvmField
  var rule = RepeatRule()

  fun createStringStream(data: List<String>): Callable<String> {
    return Callable.create<String> {
      val timer = Timer()
      timer.scheduleAtFixedRate(object : TimerTask() {
        var index = 0
        override fun run() {
          it.onResult(data[index])
          index++
          if (index >= data.size) {
            it.onComplete()
            timer.cancel()
          }
        }
      }, 0, 100)
    }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.IO)
  }

  @Test
  fun basicTest() {

    var result: String? = null
    Callable.single { "hello" }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.COMP)
      .call({
        result = it
      }, {
        Assert.fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { result != null }

    Assert.assertEquals("hello", result)
  }

  @Test
  fun basicMapTest() {
    var result: String? = null

    Callable
      .single { "hello" }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.IO)
      .map { "$it, how are you" }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.COMP)
      .call({
        result = it
      }, {
        Assert.fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { result != null }

    Assert.assertEquals("hello, how are you", result)

  }

  @Test
  fun basicFlatMapTest() {
    var result: String? = null

    Callable
      .single { "hello" }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.IO)
      .flatMap { value ->
        Callable.single { "$value, how are you" }
      }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.IO)
      .call({
        result = it
      }, {
        Assert.fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { result != null }

    Assert.assertEquals("hello, how are you", result)

  }

  @Test
  fun dataStreamTest() {
    var complete = false
    var result = ""
    val data = listOf("hello,", "how", "are", "you?", "I", "am", "good,", "thanks")

    createStringStream(data)
      .doFinally { complete = true }
      .call({
        result += " $it"
      }, {
        Assert.fail(it.message)
      })

    Awaitility
      .await()
      .atMost(15000, TimeUnit.MILLISECONDS)
      .until { complete }

    Assert.assertEquals(" hello, how are you? I am good, thanks", result)

  }

  @Test
  @Repeat(times = 100, threads = 16)
  fun streamMapTest() {
    var complete = false
    var result = ""
    val data = listOf("hello,", "how", "are", "you?", "I", "am", "good,", "thanks")

    createStringStream(data)
      .map { " $it" }
      .doFinally { complete = true }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.COMP)
      .call({
        result += it
      }, {
        Assert.fail(it.message)
      })

    Awaitility
      .await()
      .atMost(1500, TimeUnit.MILLISECONDS)
      .until { complete }

    Assert.assertEquals(" hello, how are you? I am good, thanks", result)

  }

  @Test
  @Repeat(times = 1000, threads = 16)
  fun streamFlatMapTest() {
    var complete = false
    var result = ""

    createStringStream(listOf("hello,", "how", "are", "you?", "I", "am", "good,", "thanks"))
      .flatMap { Callable.single { " $it" } }
      .doFinally { complete = true }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.COMP)
      .call({
        result += it
      }, {
        Assert.fail(it.message)
      })

    Awaitility
      .await()
      .atMost(1500, TimeUnit.MILLISECONDS)
      .until { complete }

    Assert.assertEquals(" hello, how are you? I am good, thanks", result)

  }

  @Test
  fun mixedTest() {
    var complete = false
    Callable
      .single {
        //some quick running action
        true
      }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.IO)
      .map {
        (0..10000).forEach {
          //fake some work
        }
        it
      }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.COMP)
      .flatMap { value ->
        Callable.create<Boolean> {
          val timer = Timer()
          timer.schedule(object : TimerTask() {
            override fun run() {
              it.onResult(value)
              it.onComplete()
              timer.cancel()
            }
          }, (Math.random() * 10000).toLong())
        }
      }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.COMP)
      .map {
        it
      }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.COMP)
      .call({
        complete = it
      }, {
        Assert.fail(it.message)
      })

    Awaitility
      .await()
      .atMost(15000, TimeUnit.MILLISECONDS)
      .until { complete }

    Assert.assertEquals(complete, true)

  }

}