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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.face.client.FaceBridge
import io.blockv.face.client.FaceView
import io.blockv.face.client.ViewFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class ImageProgressFace(vatom: Vatom, face: Face, bridge: FaceBridge) : FaceView(vatom, face, bridge) {

  private lateinit var container: FrameLayout
  private lateinit var fullImageContainer: FrameLayout
  private lateinit var emptyImageView: ImageView
  private lateinit var fullImageView: ImageView
  private lateinit var progressLabel: TextView
  private val config: Config
  private var originalWidth: Int = 0
  private var originalHeight: Int = 0

  init {
    config = if (face.property.config.has("direction")) {
      Config(face.property.config)
    } else
      Config(vatom.private ?: JSONObject())
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

    val context = inflater.context

    this.container = FrameLayout(context)
    fullImageContainer = FrameLayout(context)
    emptyImageView = ImageView(context)
    fullImageView = ImageView(context)
    progressLabel = TextView(context)
    fullImageContainer.addView(fullImageView)
    this.container.addView(emptyImageView)
    this.container.addView(fullImageContainer)
    progressLabel.left = 10
    progressLabel.top = 0
    progressLabel.height = 20
    progressLabel.gravity = Gravity.END
    progressLabel.textSize = 13f
    progressLabel.setBackgroundColor(Color.TRANSPARENT)
    progressLabel.setTextColor(Color.BLACK)
    progressLabel.visibility = if (config.showPercentage) View.VISIBLE else View.GONE

    this.container
      .addView(
        progressLabel,
        FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
      )

    this.container.addOnLayoutChangeListener { v, left, top, right, bottom, leftWas, topWas, rightWas, bottomWas ->
      if (left != leftWas || right != rightWas || bottom != bottomWas || top != topWas) {
        layout()
      }
    }

    return this.container
  }

  override fun onLoad(handler: FaceView.LoadHandler) {

    val empty = vatom.property.getResource(config.emptyImage)
    val full = vatom.property.getResource(config.fullImage)

    if (empty != null && full != null) {
      handler.collect(
        bridge.resourceManager
          .getFile(empty)
          .observeOn(Schedulers.io())
          .flatMap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(it.absolutePath, options)
            originalWidth = options.outWidth
            originalHeight = options.outHeight
            Single.zip(
              listOf(
                bridge.resourceManager
                  .getBitmap(empty, this.container.width, this.container.height),
                bridge.resourceManager
                  .getBitmap(full, this.container.width, this.container.height)
              )
            ) { items ->
              val out = ArrayList<Bitmap>(2)
              out.add(items[0] as Bitmap)
              out.add(items[1] as Bitmap)
              out
            }
          }
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            emptyImageView.setImageBitmap(it[0])
            fullImageView.setImageBitmap(it[1])
            layout()
            handler.onComplete()
          }, {
            handler.onError(it)
          })
      )

    } else
      handler.onError(NullPointerException("No image resource found"))
  }

  override fun onVatomChanged(oldVatom: Vatom, newVatom: Vatom): Boolean {
    layout()
    return false
  }

  class Config(
    val emptyImage: String,
    val fullImage: String,
    val direction: String,
    val paddingStart: Float,
    val paddingEnd: Float,
    val showPercentage: Boolean
  ) {
    constructor(data: JSONObject) : this(
      data.optString("empty_image", "BaseImage"),
      data.optString("full_image", "ActivatedImage"),
      data.optString("direction", "up"),
      data.optDouble("padding_start", 0.0).toFloat(),
      data.optDouble("padding_end", 0.0).toFloat(),
      data.optBoolean("show_percentage", false)
    )
  }

  private fun layout() {
    // Set progress label
    val cloningScore = vatom.property.cloningScore ?: 0f
    val progress = Math.min(1.0f, Math.max(0.0f, cloningScore))
    val progressPercent = (progress * 100.0).toInt()
    progressLabel.text = "$progressPercent%"

    var paddingStart = config.paddingStart
    var paddingEnd = config.paddingEnd
    val direction = config.direction.toLowerCase()

    val imageWidth = this.fullImageView.width.toFloat()
    val imageHeight = this.fullImageView.height.toFloat()

    val imageData = getSizeOffset(this.fullImageView)
    val actualWidth = imageData[2].toFloat()
    val actualHeight = imageData[3].toFloat()
    val offsetLeft = imageData[0].toFloat()
    val offsetTop = imageData[1].toFloat()
    val offsetRight = imageWidth - offsetLeft - actualWidth
    val offsetBottom = imageHeight - offsetTop - actualHeight

    when (direction) {
      "down", "up" -> {

        paddingStart *= (actualHeight / originalHeight)
        paddingEnd *= (actualHeight / originalHeight)

        val offsetRange = actualHeight - paddingStart - paddingEnd
        val offset =
          ((1 - progress) * offsetRange + if (direction == "up") offsetTop + paddingEnd else offsetBottom + paddingStart) * if (direction == "up") 1 else -1

        fullImageContainer.y = offset
        fullImageView.y = -offset
      }
      "right", "left" -> {

        paddingStart *= (actualWidth / originalWidth)
        paddingEnd *= (actualWidth / originalWidth)

        val offsetRange = actualWidth - paddingStart - paddingEnd
        val offset =
          ((1 - progress) * offsetRange + if (direction == "left") offsetRight + paddingStart else offsetLeft + paddingEnd) * if (direction == "left") 1 else -1

        fullImageContainer.x = offset
        fullImageView.x = -offset
      }
    }

  }

  /**
   * Returns the bitmap position and size inside an imageView.
   *
   * @param imageView source ImageView
   * @return 0: left, 1: top, 2: width, 3: height
   */
  fun getSizeOffset(imageView: ImageView): IntArray {
    val ret = IntArray(4)

    if (imageView.drawable == null)
      return ret

    // Get image dimensions
    // Get image matrix values and place them in an array
    val f = FloatArray(9)
    imageView.imageMatrix.getValues(f)

    // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
    val scaleX = f[Matrix.MSCALE_X]
    val scaleY = f[Matrix.MSCALE_Y]

    // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
    val d = imageView.drawable
    val origW = d.intrinsicWidth
    val origH = d.intrinsicHeight

    // Calculate the actual dimensions
    val actW = Math.round(origW * scaleX)
    val actH = Math.round(origH * scaleY)

    ret[2] = actW
    ret[3] = actH

    // Get image position
    // We assume that the image is centered into ImageView
    val imgViewW = imageView.width
    val imgViewH = imageView.height

    val top = (imgViewH - actH) / 2
    val left = (imgViewW - actW) / 2

    ret[0] = left
    ret[1] = top

    return ret
  }

  companion object {

    val factory: ViewFactory
      get() {
        return ViewFactory.wrap("native://progress-image-overlay") { vatom, face, bridge ->
          ImageProgressFace(vatom, face, bridge)
        }
      }
  }
}