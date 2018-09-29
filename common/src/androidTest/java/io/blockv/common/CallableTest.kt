package io.blockv.common

import android.support.test.runner.AndroidJUnit4
import io.blockv.common.util.Callable
import org.awaitility.Awaitility
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CallableTest {

  @Test
  fun testSingle() {
    var result: String? = null

    Callable
      .single { "hello" }
      .runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.COMP)
      .call({
        result = it
      }, {
        fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { result != null }

    assertEquals("hello", result)

  }

  @Test
  fun testMap1() {
    var result: String? = null

    Callable
      .single { "hello" }
      .map { "$it,how are you" }
      .call({
        result = it
      }, {
        fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { result != null }

    assertEquals("hello,how are you", result)

  }


  @Test
  fun testFlatMap1() {
    var result: String? = null

    Callable
      .single { "hello" }
      .flatMap { value ->
        Callable.single { "$value,how are you" }
      }
      .call({
        result = it
      }, {
        fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { result != null }

    assertEquals("hello,how are you", result)

  }

  @Test
  fun testFlatMapStream() {
    var count = 0
    var complete = false
    val data = listOf("hello,", "how", "are", "you?", "I", "am", "good,", "thanks")
    var index = 0

    Callable.create<String> {
      val timer = Timer()
      timer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
          it.onResult(data[index])
          index++
          if (index >= data.size) {
            it.onComplete()
            timer.cancel()
          }
        }
      }, 0, 1000)
    }
      .flatMap { value ->
        Callable.single { "($value)" }
      }.doFinally { complete = true }
      .call({
        count++
      }, {
        fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { complete }

    assertEquals(8, count)

  }


  @Test
  fun testFlatMapStream1() {
    var count = 0
    var complete = false
    val data = listOf("hello,", "how", "are", "you?", "I", "am", "good,", "thanks")

    Callable.create<String> {
      val timer = Timer()
      var index = 0
      timer.scheduleAtFixedRate(object : TimerTask() {
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
      .runOn(Callable.Scheduler.IO)
      .flatMap { _ ->
        Callable.create<String> {
          val timer = Timer()
          var index = 0
          timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
              it.onResult("(${data[index]})")
              index++
              if (index >= data.size) {
                it.onComplete()
                timer.cancel()
              }
            }
          }, 0, 100)
        }
      }
      .runOn(Callable.Scheduler.COMP)
      .doFinally { complete = true }
      .call({
        count++
      }, {
        fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { complete }

    assertEquals(64, count)

  }


  @Test
  fun testFlatMapStream1Map() {
    var count = 0
    var complete = false
    val data = listOf("hello,", "how", "are", "you?", "I", "am", "good,", "thanks")

    Callable.create<String> {
      val timer = Timer()
      var index = 0
      timer.scheduleAtFixedRate(object : TimerTask() {
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
      .runOn(Callable.Scheduler.IO)
      .map {
        "($it)"
      }
      .runOn(Callable.Scheduler.COMP)
      .flatMap { _ ->
        Callable.create<String> {
          val timer = Timer()
          var index = 0
          timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
              it.onResult("(${data[index]})")
              index++
              if (index >= data.size) {
                it.onComplete()
                timer.cancel()
              }
            }
          }, 0, 100)
        }
      }
      .runOn(Callable.Scheduler.IO)
      .map {
        "($it)"
      }
      .runOn(Callable.Scheduler.COMP)
      .doFinally { complete = true }
      .call({
        count++
      }, {
        fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { complete }

    assertEquals(64, count)

  }
  @Test
  fun testMap3() {
    var result: String? = null

    Callable
      .single { "hello" }
      .map { "$it,how are you?" }
      .map { "$it I am good, thanks" }
      .call({
        result = it
      }, {
        fail(it.message)
      })

    Awaitility
      .await()
      .atMost(10000, TimeUnit.MILLISECONDS)
      .until { result != null }

    assertEquals("hello,how are you? I am good, thanks", result)

  }

  @Test
  fun testStream() {
    var complete = false
    var result = ""
    val data = listOf("hello,", "how", "are", "you?", "I", "am", "good,", "thanks")
    var index = 0

    Callable.create<String> {
      val timer = Timer()
      timer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
          it.onResult(data[index])
          index++
          if (index >= data.size) {
            it.onComplete()
            timer.cancel()
          }
        }
      }, 0, 1000)
    }
      .doFinally { complete = true }
      .call({
        result += " $it"
      }, {
        fail(it.message)
      })

    Awaitility
      .await()
      .atMost(15000, TimeUnit.MILLISECONDS)
      .until { complete }

    assertEquals(" hello, how are you? I am good, thanks", result)

  }

  @Test
  fun testStreamMap() {
    var complete = false
    var result = ""
    val data = listOf("hello,", "how", "are", "you?", "I", "am", "good,", "thanks")
    var index = 0

    Callable.create<String> {
      val timer = Timer()
      timer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
          it.onResult(data[index])
          index++
          if (index >= data.size) {
            it.onComplete()
            timer.cancel()
          }
        }
      }, 0, 1000)
    }.runOn(Callable.Scheduler.COMP)
      .returnOn(Callable.Scheduler.MAIN)
      .map {
        "-$it"
      }
      .doFinally { complete = true }
      .call({
        result += " $it"
      }, {
        fail(it.message)
      })

    Awaitility
      .await()
      .atMost(15000, TimeUnit.MILLISECONDS)
      .until { complete }

    assertEquals(" -hello, -how -are -you? -I -am -good, -thanks", result)

  }

}