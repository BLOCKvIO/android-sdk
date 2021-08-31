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
package io.blockv.faces

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.faces.imageface.R
import io.blockv.face.client.FaceBridge
import io.blockv.face.client.FaceView
import io.blockv.face.client.ViewFactory
import io.reactivex.android.schedulers.AndroidSchedulers

class ImageFace(vatom: Vatom, face: Face, bridge: FaceBridge) : FaceView(vatom, face, bridge) {

  private lateinit var imageView: ImageView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    this.imageView = inflater.inflate(R.layout.view_blockv_native_image, container, false) as ImageView
    return imageView
  }

  override fun onLoad(handler: FaceView.LoadHandler) {
    val config = Config(face)
    if (config.scale.toLowerCase() == "fill") {
      this.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    } else {
      this.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    val resource = vatom.property.getResource(config.image)
    if (resource != null) {
      handler.collect(
        bridge.resourceManager
          .getBitmap(resource, this.imageView.width, this.imageView.height)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            imageView.setImageBitmap(it)
            handler.onComplete()
          }, {
            handler.onError(it)
          })
      )

    } else
      handler.onError(NullPointerException("No image resource found"))
  }

  override fun onVatomChanged(oldVatom: Vatom, newVatom: Vatom): Boolean {
    return false
  }


  class Config(val image: String, val scale: String) {
    constructor(face: Face) : this(
      face.property.config.optString(
        "image", if (face.property.resources?.isNotEmpty() == true) {
          face.property.resources?.getOrNull(0)
        } else "ActivatedImage"
      ),
      face.property.config.optString("scale", "fit")
    )
  }

  companion object {

    val factory: ViewFactory
      get() {
        return ViewFactory.wrap("native://image") { vatom, face, bridge ->
          ImageFace(vatom, face, bridge)
        }
      }

  }
}