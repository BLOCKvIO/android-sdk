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
package io.blockv.rxface.client

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import io.blockv.common.internal.net.rest.auth.ResourceEncoder
import io.blockv.common.model.Resource
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


open class ResourceManagerImpl(cacheDir: File, override val resourceEncoder: ResourceEncoder) :
  ResourceManager {

  val downloads = HashMap<String, Download>()

  val disk = File(cacheDir, "blockv_resources")
  val diskCache = object : LruCache<String, File>(200 * 1024 * 1024) {
    override fun sizeOf(key: String, value: File): Int {
      return value.length().toInt()
    }
  }
  val imageCache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8).toInt()) {
    override fun sizeOf(key: String, value: Bitmap): Int {
      return value.byteCount
    }
  }

  init {
    disk.mkdirs()
    disk.listFiles().forEach {
      if (!it.name.endsWith(".download")) //don't add files that are not complete
      {
        diskCache.put(it.name, it)
      }
    }
  }

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

  override fun getFile(resource: Resource): Single<File> {
    return Single.fromCallable {
      getFileFromDisk(resource.url)
    }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .flatMap {
        if (it.isEmpty()) {
          synchronized(downloads)
          {
            if (!downloads.containsKey(resource.url)) {
              downloads[resource.url] =
                Download(resourceEncoder.encodeUrl(resource.url), disk)
            }
            downloads[resource.url]!!.download()
              .doFinally {
                synchronized(downloads)
                {
                  downloads.remove(resource.url)
                }
              }
          }
        } else {
          Single.fromCallable<File> { it.value }
        }
      }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
  }

  override fun getInputStream(resource: Resource): Single<InputStream> {

    return getFile(resource)
      .map<InputStream> { file ->
        FileInputStream(file)
      }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
  }

  override fun getBitmap(resource: Resource): Single<Bitmap> {
    return getBitmap(resource, -1, -1)
  }

  override fun getBitmap(resource: Resource, width: Int, height: Int): Single<Bitmap> {
    val key = hash(resource.url + "${width}x$height")
    return Single.fromCallable {
      Optional(imageCache.get(key))
    }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .flatMap {
        if (it.isEmpty()) {
          getFile(resource)
            .map { image ->
              var reqWidth = width
              var reqHeight = height
              if (reqWidth <= 0 || reqHeight <= 0) {
                reqWidth = Resources.getSystem().displayMetrics.widthPixels
                reqHeight = Resources.getSystem().displayMetrics.heightPixels
              }
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
              imageCache.put(key, bitmap)
              bitmap
            }
        } else
          Single.fromCallable{ it.value }
      }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
  }

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


  class Download(val resource: String, private val cacheDir: File) {

    @Volatile
    private var disposable: Disposable? = null

    private val resultEmitters: HashSet<SingleEmitter<File>> = HashSet()

    fun download(): Single<File> {
      return Single.create<File> { emitter ->
        synchronized(resultEmitters) {
          resultEmitters.add(emitter)
          if (disposable == null || disposable!!.isDisposed) {
            disposable = Single.fromCallable {
              val connection = Request(resource, 10000, 10000).connect()
              try {
                val responseCode: Int = connection.responseCode
                if (responseCode == 200) {
                  val input = DataInputStream(connection.inputStream)
                  val file = File(cacheDir, hash(resource) + ".download")
                  file.createNewFile()
                  val out = FileOutputStream(file)
                  var read: Int
                  val buffer = ByteArray(16 * 1024)
                  do {
                    read = input.read(buffer, 0, buffer.size)
                    if (read != -1) {
                      out.write(buffer, 0, read)
                    }
                  } while (read != -1)
                  out.flush()
                  out.close()
                  val outFile = File(cacheDir, hash(resource))
                  file.renameTo(outFile)
                  outFile
                } else {
                  val input = DataInputStream(connection.errorStream)
                  val out = ByteArrayOutputStream()
                  var read: Int
                  val buffer = ByteArray(16 * 1024)
                  do {
                    read = input.read(buffer, 0, buffer.size)
                    if (read != -1) {
                      out.write(buffer, 0, read)
                    }
                  } while (read != -1)

                  out.flush()
                  out.close()
                  throw Exception(String(out.toByteArray()))
                }

              } finally {
                connection.disconnect()
              }

            }
              .doFinally {
                synchronized(resultEmitters) {
                  resultEmitters.remove(emitter)
                  if (resultEmitters.size == 0) {
                    disposable?.dispose()
                  }
                }
              }
              .subscribe({ file ->
                synchronized(resultEmitters) {
                  resultEmitters.forEach {
                    it.onSuccess(file)
                  }
                }
              }) {
                synchronized(resultEmitters) {
                  val throwable = it
                  resultEmitters.forEach {
                    it.onError(throwable)
                  }
                }
              }
          }
        }
      }
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
    }
  }

}