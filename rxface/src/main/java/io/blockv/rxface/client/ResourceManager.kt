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

import android.graphics.Bitmap
import io.blockv.common.internal.net.rest.auth.ResourceEncoder
import io.blockv.common.model.Resource
import io.reactivex.Single
import java.io.File
import java.io.InputStream

/**
 * Provides methods for a FaceView to fetch it resources.
 *
 * These resources can come either from a cache or via the network.
 */
interface ResourceManager {

  /**
   * Encoder used to add access parameters to the resource url.
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
  fun getFile(resource: Resource): Single<File>

  /**
   * Provides a input stream representing the specified resource.
   *
   * This can either come from directly from cache or via the network.
   *
   * @param resource is the vAtom resource that must be fetched.
   * @return new Callable<InputStream>
   */
  fun getInputStream(resource: Resource): Single<InputStream>

  /**
   * Provides a bitmap representing the specified resource. The resource is required to
   * be of type image.
   *
   * This can either come from directly from cache or via the network.
   *
   * @param resource is the vAtom resource that must be fetched.
   * @return new Callable<Bitmap>
   */
  fun getBitmap(resource: Resource): Single<Bitmap>

  /**
   * Provides a scaled bitmap representing the specified resource. The resource is required to
   * be of type image.
   *
   * This can either come from directly from cache or via the network.
   *
   * @param resource is the vAtom resource that must be fetched.
   * @param width is the max width of the image. -1 defaults to screen width.
   * @param height is the max height of the image. -1 defaults to screen height.
   * @return new Callable<Bitmap>
   */
  fun getBitmap(resource: Resource, width: Int, height: Int): Single<Bitmap>

}