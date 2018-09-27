package io.blockv.face.client

import android.graphics.Bitmap
import io.blockv.common.internal.net.rest.auth.ResourceEncoder
import io.blockv.common.model.Resource
import io.blockv.common.util.Callable
import java.io.File
import java.io.InputStream

interface ResourceManager {

  val resourceEncoder: ResourceEncoder

  fun getFile(resource: Resource): Callable<File>

  fun getInputStream(resource: Resource): Callable<InputStream>

  fun getBitmap(resource: Resource): Callable<Bitmap>

  fun getBitmap(resource: Resource, width: Int, height: Int): Callable<Bitmap>

}