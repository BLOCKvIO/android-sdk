package io.blockv.rxface.client

import android.graphics.Bitmap
import io.blockv.common.internal.net.rest.auth.ResourceEncoder
import io.blockv.common.model.Resource
import io.blockv.common.util.Callable
import java.io.File
import java.io.InputStream

class ResourceManagerWrapper(private val resourceManager: ResourceManager) :
  io.blockv.face.client.manager.ResourceManager {
  override val resourceEncoder: ResourceEncoder
    get() = resourceManager.resourceEncoder

  override fun getFile(resource: Resource): Callable<File> {
    return Callable.single { resourceManager.getFile(resource).blockingGet() }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.IO)
  }

  override fun getInputStream(resource: Resource): Callable<InputStream> {
    return Callable.single { resourceManager.getInputStream(resource).blockingGet() }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.IO)
  }

  override fun getBitmap(resource: Resource): Callable<Bitmap> {
    return Callable.single { resourceManager.getBitmap(resource).blockingGet() }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.IO)
  }

  override fun getBitmap(resource: Resource, width: Int, height: Int): Callable<Bitmap> {
    return Callable.single { resourceManager.getBitmap(resource, width, height).blockingGet() }
      .runOn(Callable.Scheduler.IO)
      .returnOn(Callable.Scheduler.IO)
  }
}