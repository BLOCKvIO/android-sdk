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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Bitmap
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import io.blockv.common.model.Face
import io.blockv.common.model.Resource
import io.blockv.common.model.Vatom
import io.blockv.face.client.FaceBridge
import io.blockv.face.client.FaceView
import io.blockv.face.client.ViewFactory
import io.blockv.faces.layeredface.R
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

class ImageLayeredFace(vatom: Vatom, face: Face, bridge: FaceBridge) : FaceView(vatom, face, bridge) {

  private lateinit var layeredContainer: FrameLayout
  private lateinit var baseLayer: ImageView
  private val config: Config
  private val accelerateInterpolator = AccelerateInterpolator()

  private val id = Math.random() * 10000
  @get:Synchronized
  @set:Synchronized
  private var updates: Disposable? = null

  @get:Synchronized
  @set:Synchronized
  var currentLayers = HashMap<String, Resource>()


  init {
    config = if (face.property.config.length() > 0) {
      Config(face)
    } else
      Config("ActivatedImage", "fit")
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    layeredContainer = inflater.inflate(R.layout.view_blockv_native_image_layered, container, false) as FrameLayout

    return layeredContainer
  }

  override fun onLoad(handler: FaceView.LoadHandler) {

    var resource = vatom.property.getResource(config.image)
    if (resource == null)
      resource = vatom.property.getResource("ActivatedImage")

    if (resource != null) {
      handler.collect(
        bridge.resourceManager
          .getBitmap(resource, this.layeredContainer.width, this.layeredContainer.height)
          .observeOn(AndroidSchedulers.mainThread())
          .map {
            val imageView = ImageView(layeredContainer.context)
            baseLayer = imageView
            if (config.scale.toLowerCase() == "fill") {
              imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
              imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            imageView.setImageBitmap(it)
            imageView.tag = vatom.property.templateVariationId
            layeredContainer.addView(imageView)
            it
          }
          .flatMap {
            val layers = layerCache.get(vatom.id)
            if (layers != null) {
              Single.fromCallable {
                Pair<HashMap<String, Resource>, Boolean>(layers, true)//cached
              }
            } else
              bridge.vatomManager.getInventory(vatom.id, 1, 100)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { vatoms ->
                  val map = HashMap<String, Resource>()
                  vatoms.forEach { vatom ->
                    val res = vatom.property.getResource(config.image)
                    if (res != null) {
                      map[vatom.id] = res
                    }
                  }
                  Pair(map, false)
                }
          }
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap { layers ->
            currentLayers = layers.first
            construct(layers.first, HashMap(), false)
              .map {
                layers
              }
          }
          .subscribe({ layers ->

            updates =
              getLayers(if (!layers.second) layers.first else null)
                .observeOn(Schedulers.computation())
                .flatMap { updateLayers ->
                  val old = HashMap(currentLayers)
                  currentLayers = updateLayers
                  layerCache.put(vatom.id, HashMap(currentLayers))
                  construct(updateLayers, old, true)
                    .toFlowable()
                }
                .subscribe({
                }, {
                })
            collect(updates!!)
            handler.onComplete()
          }, {
            handler.onError(it)
          })
      )

    } else
      handler.onError(NullPointerException("No image resource found"))
  }

  private fun getLayers(
    initialLayers: HashMap<String, Resource>? = null
  ): Flowable<HashMap<String, Resource>> {

    val layers: HashMap<String, Resource> = if (initialLayers != null) HashMap(initialLayers) else HashMap()

    return Flowable.fromCallable {
      layers
    }.filter { initialLayers != null }
      .concatWith(
        bridge.eventManager
          .getVatomStateEvents()
          .filter { it.payload != null }
          .map {
            it.payload
          }
          .filter { event ->
            event.operation == "update"
              && event.vatomProperties.has("vAtom::vAtomType")
              && event.vatomProperties.getJSONObject("vAtom::vAtomType").has("parent_id")
          }
          .observeOn(Schedulers.computation())
          .flatMap<HashMap<String, Resource>> { event ->
            synchronized(layers)
            {
              val data = event.vatomProperties
              val vatomProp = data.getJSONObject("vAtom::vAtomType")
              val parentId = vatomProp.getString("parent_id")

              if (parentId != vatom.id) {
                if (layers.contains(event.vatomId)) {
                  layers.remove(event.vatomId)
                  Flowable.just(HashMap(layers))
                } else
                  Flowable.just(NULL_LAYERS)
              } else if (parentId == vatom.id) {
                bridge
                  .vatomManager
                  .getVatoms(event.vatomId)
                  .filter {
                    it.isNotEmpty()
                  }
                  .toFlowable()
                  .flatMap<HashMap<String, Resource>> {
                    val resource = it[0].property.getResource(config.image)
                    if (resource != null) {
                      layers[event.vatomId] = resource
                      Flowable.just(HashMap(layers))
                    } else
                      Flowable.just(NULL_LAYERS)
                  }
              } else
                Flowable.just(NULL_LAYERS)
            }
          }.mergeWith(
            Single.timer(300, TimeUnit.MILLISECONDS)
              .flatMap {
                bridge
                  .vatomManager
                  .getInventory(vatom.id, 1, 100)
              }
              .observeOn(Schedulers.computation())
              .map { vatoms ->
                synchronized(layers)
                {
                  layers.clear()
                  vatoms.forEach { vatom ->
                    val resource = vatom.property.getResource(config.image)
                    if (resource != null) {
                      layers[vatom.id] = resource
                    }
                  }
                  HashMap(layers)
                }
              }
              .toFlowable())
          .retryWhen { error ->
            error.delay(3, TimeUnit.SECONDS)
          })
      .observeOn(AndroidSchedulers.mainThread())
  }

  @Synchronized
  private fun construct(
    newLayers: HashMap<String, Resource>,
    oldLayers: HashMap<String, Resource>,
    animate: Boolean = false
  ): Single<HashSet<String>> {

    return Single.fromCallable {

      val removed = HashSet(oldLayers.keys)
      val added = HashMap<String, Resource>()

      newLayers.forEach { layer ->
        if (oldLayers.containsKey(layer.key)) {
          removed.remove(layer.key)
        } else
          if (layer.key != vatom.id) {

            added[layer.key] = layer.value
          }
      }
      Pair(removed, added)
    }
      .subscribeOn(Schedulers.computation())
      .observeOn(AndroidSchedulers.mainThread())
      .map {
        var startDelay = 0L
        it.first.forEach { id ->

          val layer = layeredContainer.findViewWithTag<ImageView>(id)

          if (layer != null) {

            if (animate) {
              layer.animate()
                .alpha(0f)
                .setDuration(300)
                .setStartDelay(startDelay)
                .setInterpolator(accelerateInterpolator)
                .setListener(object : AnimatorListenerAdapter() {
                  override fun onAnimationEnd(animation: Animator) {
                    layer.alpha = 0f
                    layeredContainer.removeView(layer)
                  }
                })
                .start()
              startDelay += 300L
            } else {
              layeredContainer.removeView(layer)
            }
          }
        }
        it.second
      }
      .observeOn(Schedulers.io())
      .flatMap { added ->
        fetchBitmaps(added.map { Pair(it.key, it.value) }, layeredContainer.width, layeredContainer.height)
      }
      .observeOn(AndroidSchedulers.mainThread())
      .map { layers ->

        val outList = HashSet<String>()
        layers.forEach { layer ->
          outList.add(layer.first)
          val current = layeredContainer.findViewWithTag<ImageView>(layer.first)
          if (current == null) {
            val imageView = ImageView(layeredContainer.context)
            if (config.scale.toLowerCase() == "fill") {
              imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
              imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            imageView.setImageBitmap(layer.second)
            imageView.tag = layer.first
            if (animate) {
              imageView.alpha = 0f
              layeredContainer.addView(imageView)
              imageView.bringToFront()
              imageView.animate()
                .alpha(1f)
                .setDuration(200)
                .setStartDelay(0)
                .setInterpolator(accelerateInterpolator)
                .setListener(object : AnimatorListenerAdapter() {
                  override fun onAnimationEnd(animation: Animator) {
                    imageView.alpha = 1f
                  }
                })
                .start()
            } else {
              layeredContainer.addView(imageView)
              imageView.bringToFront()
            }

          } else
            current.setImageBitmap(layer.second)
        }

        outList
      }

  }

  private fun fetchBitmaps(
    images: List<Pair<String, Resource>>,
    width: Int,
    height: Int
  ): Single<ArrayList<Pair<String, Bitmap>>> {

    if (images.isEmpty()) {
      return Single.just(ArrayList())
    }
    return Single.zip(images.map { bridge.resourceManager.getBitmap(it.second, width, height) })
    { vals ->
      val out = ArrayList<Pair<String, Bitmap>>()
      (0 until vals.size).forEach {
        val bitmap: Bitmap = vals[it] as Bitmap
        out.add(Pair(images[it].first, bitmap))
      }
      out
    }.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  private fun getResourceLayers(vatoms: Set<Vatom>): Single<ArrayList<Pair<String, Resource>>> {
    return Single.fromCallable {
      val layers = ArrayList<Pair<String, Resource>>()
      val set = HashSet<String>()
      val res = vatom.property.getResource(config.image)
      if (res != null) {
        set.add(vatom.property.templateVariationId)
        layers.add(Pair(vatom.property.templateVariationId, res))
      }
      vatoms.forEach { vatom ->
        if (!set.contains(vatom.property.templateVariationId)) {
          val resource = vatom.property.getResource(config.image)
          if (resource != null) {
            set.add(vatom.property.templateVariationId)
            layers.add(Pair(vatom.property.templateVariationId, resource))
          }
        }
      }
      layers
    }
      .subscribeOn(Schedulers.computation())
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun onVatomChanged(oldVatom: Vatom, newVatom: Vatom): Boolean {
    updates?.dispose()
    vatom = newVatom
    val old = currentLayers
    currentLayers = layerCache.get(vatom.id) ?: HashMap()
    collect(
      construct(currentLayers, old, false)
        .doFinally {
          updates?.dispose()
          updates = getLayers()
            .observeOn(Schedulers.computation())
            .flatMap { updateLayers ->
              val oldLayers = currentLayers
              currentLayers = updateLayers
              layerCache.put(vatom.id, HashMap(currentLayers))
              construct(updateLayers, oldLayers, true)
                .toFlowable()
            }
            .subscribe({
            }, {})
          collect(updates!!)
        }
        .subscribe({}, {})
    )
    return false
  }


  class Config(val image: String, val scale: String) {
    constructor(face: Face) : this(
      face.property.config.optString(
        "layerImage", "LayeredImage"
      ),
      face.property.config.optString("scale", "fit")
    )
  }

  companion object {

    internal val NULL_LAYERS = HashMap<String, Resource>()

    internal val layerCache = LruCache<String, HashMap<String, Resource>>(20)

    val factory: ViewFactory
      get() {
        return ViewFactory.wrap("native://layered-image") { vatom, face, bridge ->
          ImageLayeredFace(vatom, face, bridge)
        }
      }

  }
}