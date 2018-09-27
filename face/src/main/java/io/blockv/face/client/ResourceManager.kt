package io.blockv.face.client

import android.graphics.Bitmap
import io.blockv.common.internal.net.rest.auth.ResourceEncoder
import io.blockv.common.model.Resource
import io.blockv.common.util.Callable
import java.io.File
import java.io.InputStream

/**
 * Provides methods for a FaceView to fetch it resources.
 *
 * These resources can come either from a cache or via the network.
 */
interface ResourceManager {

  /**
   * Encoder used at add access parameters to the resource url.
   */
  val resourceEncoder: ResourceEncoder

  /**
   * Provides a file representing the specified resource.
   *
   * This can either come from directly from cache or via the network.
   *
   * @param resource is the vAtom resource that must be fetched.
   * @return new Callable<File>
   */
  fun getFile(resource: Resource): Callable<File>

  /**
   * Provides a input stream representing the specified resource.
   *
   * This can either come from directly from cache or via the network.
   *
   * @param resource is the vAtom resource that must be fetched.
   * @return new Callable<InputStream>
   */
  fun getInputStream(resource: Resource): Callable<InputStream>

  /**
   * Provides a bitmap representing the specified resource. The resource requires to
   * be of the type image.
   *
   * This can either come from directly from cache or via the network.
   *
   * @param resource is the vAtom resource that must be fetched.
   * @return new Callable<Bitmap>
   */
  fun getBitmap(resource: Resource): Callable<Bitmap>

  /**
   * Provides a scaled bitmap representing the specified resource. The resource requires to
   * be of the type image.
   *
   * This can either come from directly from cache or via the network.
   *
   * @param resource is the vAtom resource that must be fetched.
   * @param width is the max width of the image. -1 defaults to screen width.
   * @param height is the max height of the image. -1 defaults to screen height.
   * @return new Callable<Bitmap>
   */
  fun getBitmap(resource: Resource, width: Int, height: Int): Callable<Bitmap>

}