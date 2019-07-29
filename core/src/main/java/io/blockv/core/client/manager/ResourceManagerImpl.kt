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
package io.blockv.core.client.manager

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import io.blockv.common.internal.net.rest.auth.ResourceEncoder
import io.blockv.common.internal.repository.Preferences
import io.blockv.common.model.AssetProvider
import io.blockv.common.util.Optional
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.SoftReference
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.Semaphore
import kotlin.collections.HashMap

class ResourceManagerImpl(
  cacheDir: File,
  private val encoder: ResourceEncoder,
  private val preferences: Preferences
) :
  ResourceManager {

  override val assetProviders: List<AssetProvider>?
    get() = preferences.assetProviders

  @Throws(ResourceEncoder.MissingAssetProviderException::class)
  override fun encodeUrl(url: String): String {
    return encoder.encodeUrl(url)
  }

  val disk = File(cacheDir, "blockv_resources")
  val diskCache = object : LruCache<String, File>(200 * 1024 * 1024) {
    override fun sizeOf(key: String, value: File): Int {
      return value.length().toInt()
    }
  }
  val imageCache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8).toInt()) {
    override fun entryRemoved(evicted: Boolean, key: String?, oldValue: Bitmap?, newValue: Bitmap?) {
      super.entryRemoved(evicted, key, oldValue, newValue)
      if (evicted && key != null && oldValue != null) {
        synchronized(this)
        {
          imageSoftCache[key] = SoftReference(oldValue)
        }
      }
    }

    override fun sizeOf(key: String, value: Bitmap): Int {
      return value.byteCount
    }
  }
  val imageSoftCache = HashMap<String, SoftReference<Bitmap>>()

  val maxDownloads = PermitLock(10)
  val maxImageProcess = PermitLock(10)

  init {
    disk.mkdirs()
    disk.listFiles().forEach {
      if (!it.name.endsWith(".download")) //don't add files that are not complete
      {
        diskCache.put(it.name, it)
      } else {
        it.delete()
      }
    }
  }


  private val downloadMap = HashMap<String, Download>()
  private val fileMap = HashMap<String, Observable<File>>()

  private val imageMap = HashMap<String, Observable<Bitmap>>()

  @Synchronized
  fun getFileFromDisk(url: String): Optional<File> {

    val key = hash(url)

    val file = diskCache.get(key)

    if (file != null) {
      if (file.exists()) {
        return Optional(file)
      } else {
        diskCache.remove(key)
      }
    }

    return Optional(null)
  }

  override fun getFile(url: String): Single<File> {
    return Single.create<File> { emitter ->
      maxDownloads.prioritize(url)

      synchronized(fileMap)
      {
        if (!fileMap.containsKey(url)) {
          fileMap[url] = Observable.fromCallable {
            getFileFromDisk(url)
          }
            .subscribeOn(Schedulers.io())
            .flatMap {
              if (it.isEmpty()) {
                synchronized(downloadMap)
                {
                  if (downloadMap[url] == null) {
                    downloadMap[url] = Download(url, hash(url))
                  }
                  downloadMap[url]!!.file
                }
              } else {
                Observable.just(it.value!!)
              }
            }
            .subscribeOn(Schedulers.io())
            .share()
        }

        emitter.setDisposable(
          fileMap[url]!!
            .map { Optional(it) }
            .defaultIfEmpty(Optional(null))
            .map { it.value ?: throw RetryException() }
            .retryWhen { error ->
              error.flatMap {
                if (it is RetryException) {
                  Observable.just(it)
                } else
                  Observable.error(it)
              }
            }
            .subscribe({
              emitter.onSuccess(it)
            }, {
              emitter.onError(it)
            })
        )
      }
    }
      .subscribeOn(Schedulers.io())
  }

  override fun getInputStream(url: String): Single<InputStream> {
    return Single.fromCallable {
      synchronized(this)
      {
        val file = getFileFromDisk(url)
        if (file.isEmpty()) {
          synchronized(downloadMap)
          {
            if (downloadMap[url] == null) {
              downloadMap[url] = Download(url, hash(url))
            }
            downloadMap[url]!!.inputStream
          }
        } else
          FileInputStream(file.value!!)
      }
    }
  }

  override fun getBitmap(url: String): Single<Bitmap> {
    return getBitmap(url, -1, -1)
  }

  override fun getBitmap(url: String, width: Int, height: Int): Single<Bitmap> {
    return Single.create<Bitmap> { emitter ->
      var reqWidth = width
      var reqHeight = height
      if (reqWidth <= 0 || reqHeight <= 0) {
        reqWidth = Resources.getSystem().displayMetrics.widthPixels
        reqHeight = Resources.getSystem().displayMetrics.heightPixels
      }
      synchronized(imageMap)
      {
        val key = hash(url + "${reqWidth}x$reqHeight")
        synchronized(imageCache)
        {
          val image = Optional(imageCache.get(key))
          if (!image.isEmpty()) {
            emitter.onSuccess(image.value!!)
          } else {
            maxImageProcess.prioritize(key)
            if (!imageMap.containsKey(key)) {
              imageMap[key] = Observable.fromCallable {
                synchronized(imageCache)
                {
                  var cached = imageCache.get(key)
                  if (cached == null) {
                    val ref = imageSoftCache.remove(key)
                    cached = ref?.get()
                    if (cached != null) {
                      imageCache.put(key, cached)
                    }
                  }
                  Optional(cached)
                }
              }
                .subscribeOn(Schedulers.io())
                .flatMap {
                  if (it.isEmpty()) {
                    try {
                      maxImageProcess.acquire(key)
                      getFile(url)
                        .toObservable()
                        .map { image ->
                          val options = BitmapFactory.Options()
                          options.inJustDecodeBounds = true
                          BitmapFactory.decodeFile(image.absolutePath, options)
                          options.inJustDecodeBounds = false
                          options.inSampleSize = calculateInSampleSize(
                            options,
                            reqWidth,
                            reqHeight
                          )
                          val bitmap = BitmapFactory.decodeFile(image.absolutePath, options)
                            ?: throw NullPointerException("Bitmap is null!")
                          synchronized(imageCache)
                          {
                            imageCache.put(key, bitmap)
                          }
                          bitmap
                        }
                    } finally {
                      maxImageProcess.release()
                    }
                  } else
                    Observable.fromCallable<Bitmap> {
                      it.value!!
                    }
                }
                .subscribeOn(Schedulers.io())
                .share()
            }
            emitter.setDisposable(imageMap[key]!!
              .map { Optional(it) }
              .defaultIfEmpty(Optional(null))
              .map {
                it.value ?: throw RetryException()
              }
              .retryWhen { error ->
                error.flatMap {
                  if (it is RetryException) {
                    Observable.just(it)
                  } else
                    Observable.error(it)
                }
              }
              .subscribe({
                emitter.onSuccess(it)
              }, {
                emitter.onError(it)
              })
            )
          }
        }
      }
    }
      .subscribeOn(Schedulers.io())
  }

  class RetryException : Exception()

  class Request(
    private val endpoint: String,
    private val readTimeout: Int,
    private val connectTimeout: Int
  ) {
    val method: String = "GET"

    fun connect(): HttpURLConnection {

      val url = URL(endpoint)
      val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
      connection.readTimeout = readTimeout
      connection.connectTimeout = connectTimeout
      connection.requestMethod = method
      connection.useCaches = true
      connection.doInput = true
      return connection
    }
  }

  companion object {

    private const val ALGORITHM = "SHA-256"

    fun hash(data: String): String {
      return hash(data.toByteArray())
    }

    private fun hash(data: ByteArray): String {
      val digest: MessageDigest
      try {
        digest = MessageDigest.getInstance(ALGORITHM)
        digest.update(data)
        return hex(digest.digest())
      } catch (e: NoSuchAlgorithmException) {
        throw RuntimeException(e)
      }

    }

    private fun hex(bytes: ByteArray): String {
      val sb = StringBuilder()
      for (i in bytes.indices) {
        val hex = Integer.toHexString((0xFF and bytes[i].toInt()))
        if (hex.length == 1) {
          sb.append('0')
        }
        sb.append(hex)
      }
      return sb.toString()
    }

    fun calculateInSampleSize(
      options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
      val height = options.outHeight
      val width = options.outWidth
      var inSampleSize = 1
      if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
          inSampleSize *= 2
        }
      }
      return inSampleSize
    }
  }

  class PermitLock(val permits: Int) {
    private val locks = ArrayList<Permit>()
    private var state: Int = permits

    fun acquire(key: String) {
      var wait = false
      val permit = Permit(Object(), key, System.currentTimeMillis())
      synchronized(this)
      {
        if (this.state <= 0) {
          locks.add(permit)
          wait = true
        } else {
          state--
        }
      }

      if (wait) {
        permit.lock()
      }
    }

    @Synchronized
    fun prioritize(key: String) {
      locks.forEach {
        if (it.key == key) {
          it.age = System.currentTimeMillis()
        }
      }
    }

    @Synchronized
    fun release() {
      if (state < permits) {
        state++
      }

      if (locks.size > 0 && state > 0) {
        state--
        locks.sortedByDescending { it.age }
        val permit = locks.removeAt(0)
        permit.release()
      }
    }

    class Permit(val lock: Object, val key: String, var age: Long) {
      var isLocked = false
      var isReleased = false
      fun lock() {
        synchronized(lock)
        {
          synchronized(this)
          {
            if (isLocked || isReleased) return
            isLocked = true
          }
          try {
            lock.wait()
          } catch (ie: InterruptedException) {
          }
        }

      }

      @Synchronized
      fun release() {
        if (!isLocked || isReleased) {
          isReleased = true
          return
        }
        isReleased = true
        synchronized(lock)
        {
          lock.notify()
        }
      }
    }
  }

  class Lock {

    private val innerLock = Semaphore(1)

    fun lock() {
      innerLock.acquire()
    }

    fun unlock() {
      innerLock.drainPermits()
      innerLock.release()
    }

    fun isLocked(): Boolean {
      return innerLock.availablePermits() <= 0
    }

    fun tryLock(): Boolean {
      return innerLock.tryAcquire()
    }
  }

  inner class Download(
    val url: String,
    val cacheKey: String
  ) {
    private val bufferSize = 16 * 1024
    private val chunkSize = bufferSize * 64
    var chunkIndex = 0
    var isComplete = false
    val chunks = ArrayList<Pair<Lock, Int>>()
    val setupLock = Lock()
    var masterFile: File? = null

    init {
      setupLock.tryLock()
    }

    val downloader: Observable<Unit> =
      Observable.fromCallable<Unit> {
        setupLock.tryLock()
        isComplete = false
        val cached = getFileFromDisk(url)
        if (cached.isEmpty()) {
          var input: InputStream? = null
          var output: OutputStream? = null
          var connection: HttpURLConnection? = null
          var lock: Lock? = null
          chunkIndex = 0
          chunks.clear()
          try {
            //
            maxDownloads.acquire(url)
            connection = Request(encodeUrl(url), 10000, 10000).connect()
            val responseCode: Int = connection.responseCode
            if (responseCode == 200) {
              val masterFile = File(disk, "$cacheKey.download")
              masterFile.delete()
              masterFile.createNewFile()
              this.masterFile = masterFile
              output = FileOutputStream(masterFile)
              input = DataInputStream(connection.inputStream)
              var read: Int
              val buffer = ByteArray(bufferSize)
              var size = 0
              var start = true
              do {
                if (size >= chunkSize || start) {
                  try {
                    output.flush()
                  } catch (e: Exception) {
                  }
                  val chunkLock = Lock()
                  chunkLock.lock()
                  if (chunks.size > 0) {
                    chunks[chunks.size - 1] = Pair(lock!!, size)
                  }
                  chunks.add(Pair(chunkLock, 0))
                  lock?.unlock()
                  lock = chunkLock
                  size = 0
                  chunkIndex++
                  if (start) {
                    setupLock.unlock()
                    start = false
                  }
                }
                read = input.read(buffer, 0, buffer.size)
                if (read != -1) {
                  size += read
                  output.write(buffer, 0, read)
                } else {
                  if (chunks.size > 0) {
                    chunks[chunks.size - 1] = Pair(lock!!, size)
                  }
                  lock?.unlock()
                }
              } while (read != -1)
              output.flush()
              isComplete = true
            } else {
              // error on connection open, load error message and throw exception
              input = DataInputStream(connection.errorStream)
              output = ByteArrayOutputStream()
              var read: Int
              val buffer = ByteArray(16 * 1024)
              do {
                read = input.read(buffer, 0, buffer.size)
                if (read != -1) {
                  output.write(buffer, 0, read)
                }
              } while (read != -1)

              output.flush()
              throw Exception(String(output.toByteArray()))
            }
          } finally {
            maxDownloads.release()
            connection?.disconnect()
            try {
              input?.close()
            } catch (e: Exception) {
            }
            try {
              output?.close()
            } catch (e: Exception) {
            }
            lock?.unlock()
          }
        } else {
          masterFile = cached.value
          isComplete = true
          setupLock.unlock()
        }
      }.subscribeOn(Schedulers.io())
        .doFinally {
          if (isComplete) {
            val temp = masterFile
            if (temp != null) {
              val outFile = File(disk, cacheKey)
              temp.renameTo(outFile)
              diskCache.put(outFile.name, outFile)
              fileEmitter?.onNext(outFile)
              fileEmitter?.onComplete()
              downloadMap.remove(url)
            } else
              fileEmitter?.onError(Exception("file is null"))
          }
        }
        .share()

    var fileEmitter: ObservableEmitter<File>? = null

    @get:Synchronized
    val file: Observable<File> = Observable.create<File> { emitter ->
      fileEmitter = emitter
      emitter.setDisposable(downloader.subscribe({
      }, {
        emitter.onError(it)
        emitter.onComplete()
      }))
    }
      .share()

    val inputStream: InputStream
      @Synchronized get() {
        val cached = getFileFromDisk(url)
        if (!cached.isEmpty()) {
          return FileInputStream(cached.value!!)
        } else {
          return object : InputStream() {
            var error: Throwable? = null
            val download = downloader
              .subscribe({}, {
                this.error = it
              })
            var chunk = 0
            var fileStream: InputStream? = null
            val lock = Object()
            var count = -1
            @Synchronized
            override fun read(): Int {
              try {
                if (error != null) throw error!!

                if (fileStream == null) {
                  if (setupLock.isLocked()) {
                    try {
                      setupLock.lock()
                    } finally {
                      setupLock.unlock()
                    }
                    fileStream = BufferedInputStream(FileInputStream(masterFile!!))
                  }
                }
                do {
                  if (count < 0) {
                    if (chunks.size > chunk) {
                      if (chunks[chunk].first.isLocked()) {
                        try {
                          chunks[chunk].first.lock()
                        } finally {
                          chunks[chunk].first.unlock()
                        }
                      }
                      count = chunks[chunk].second
                    } else if (isComplete) {
                      return -1
                    } else {
                      synchronized(lock)
                      {
                        try {
                          lock.wait(200)
                        } catch (e: InterruptedException) {
                        }
                      }
                      continue
                    }
                  } else {
                    count--
                    if (count < 0) {
                      chunk++
                      continue
                    }
                  }
                  return fileStream!!.read()
                } while (true)
              } catch (e: Exception) {
                e.printStackTrace()
                try {
                  fileStream?.close()
                  fileStream = null
                } catch (e: Exception) {
                }
                throw e
              }
            }

            override fun close() {
              super.close()
              try {
                fileStream?.close()
                fileStream = null
              } catch (e: Exception) {
              }
              download.dispose()
            }
          }
        }
      }
  }
}

