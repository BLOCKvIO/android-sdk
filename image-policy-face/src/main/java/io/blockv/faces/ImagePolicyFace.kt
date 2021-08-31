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

import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.blockv.common.model.Face
import io.blockv.common.model.Resource
import io.blockv.common.model.Vatom
import io.blockv.face.client.FaceBridge
import io.blockv.face.client.FaceView
import io.blockv.face.client.ViewFactory
import io.blockv.faces.policyface.R
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

class ImagePolicyFace(vatom: Vatom, face: Face, bridge: FaceBridge) : FaceView(vatom, face, bridge) {

  private lateinit var imageView: ImageView
  private val config: Config

  @get:Synchronized
  @set:Synchronized
  private var childrenCount = 0

  @get:Synchronized
  @set:Synchronized
  private var updates: Disposable? = null

  @get:Synchronized
  @set:Synchronized
  private var fetchResource: Disposable? = null


  init {
    config = if (face.property.config.has("image_policy")) {
      Config(face.property.config)
    } else
      Config(vatom.private!!)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    this.imageView = inflater.inflate(R.layout.view_blockv_native_image_policy, container, false) as ImageView

    if (config.scale.toLowerCase() == "fill") {
      this.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    } else {
      this.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }
    return imageView
  }

  override fun onLoad(handler: FaceView.LoadHandler) {
    childrenCount = 0

    handler.collect(
      (//only fetch children if they are needed for the face
        if (requiresChildren())
          (bridge.vatomManager
            .getInventory(vatom.id, 1, 100)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap { vatoms ->

              childrenCount = vatoms.size

              countCache.put(vatom.id, childrenCount)

              startChildUpdates(HashSet(vatoms.map { it.id }))

              bridge.resourceManager
                .getBitmap(
                  getCurrentResource() ?: vatom.property.getResource("ActivatedImage")!!,
                  this.imageView.width,
                  this.imageView.height
                )
            })
        else
          bridge.resourceManager
            .getBitmap(
              getCurrentResource() ?: vatom.property.getResource("ActivatedImage")!!,
              this.imageView.width,
              this.imageView.height
            ))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          imageView.setImageBitmap(it)
          handler.onComplete()
        }, {
          handler.onError(it)
        })
    )

  }

  private fun requiresChildren(): Boolean {
    config.policy.forEach {
      if (it.has("count_max"))
        return true
    }
    return false
  }

  @Synchronized
  fun startChildUpdates(children: HashSet<String>? = null) {
    updates?.dispose()

    updates = getChildrenCount(children)
      .subscribe({
        childrenCount = it
        countCache.put(vatom.id, childrenCount)
        layout()
      }, {
        it.printStackTrace()
      })
  }

  private fun getChildrenCount(initalChildren: HashSet<String>? = null): Flowable<Int> {

    val layers = initalChildren ?: HashSet()

    return Flowable.fromCallable {
      layers.size
    }.concatWith(
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
        .flatMap<Int> { event ->
          synchronized(layers)
          {
            val data = event.vatomProperties
            val vatomProp = data.getJSONObject("vAtom::vAtomType")
            val parentId = vatomProp.getString("parent_id")

            if (parentId != vatom.id) {
              if (layers.contains(event.vatomId)) {
                layers.remove(event.vatomId)
                Flowable.just(layers.size)
              } else
                Flowable.just(-1)
            } else if (parentId == vatom.id) {
              if (!layers.contains(event.vatomId)) {
                layers.add(event.vatomId)
                Flowable.just(layers.size)
              } else
                Flowable.just(-1)
            } else
              Flowable.just(-1)
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
                  layers.add(vatom.id)
                }
                layers.size
              }
            }
            .toFlowable())
        .filter { it >= 0 }
        .retryWhen { error ->
          error.delay(3, TimeUnit.SECONDS)
        }
    )
      .observeOn(AndroidSchedulers.mainThread())

  }

  @Synchronized
  private fun layout() {

    val res = getCurrentResource()
    if (res != null) {
      fetchResource?.dispose()

      fetchResource = bridge.resourceManager
        .getBitmap(
          getCurrentResource() ?: vatom.property.getResource("ActivatedImage")!!,
          this.imageView.width,
          this.imageView.height
        )
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          imageView.setImageBitmap(it)
        }, {
          it.printStackTrace()
        })

      collect(fetchResource!!)
    }
  }

  override fun onVatomChanged(oldVatom: Vatom, newVatom: Vatom): Boolean {
    updates?.dispose()
    vatom = newVatom
    childrenCount = countCache[vatom.id] ?: 0
    layout()
    if (requiresChildren()) {
      startChildUpdates()
    }
    return false
  }

  @Synchronized
  fun getCurrentResource(): Resource? {

    for (policy in config.policy) {

      if (policy.has("resource")) {
        if (policy.has("count_max")) {
          val max = policy.getInt("count_max")
          if (max == childrenCount) {
            return vatom.property.getResource(policy.getString("resource"))
          }
        } else if (policy.has("field") && policy.has("value")) {

          val field = policy.getString("field")
          if (field.startsWith("private.")) {

            val privateField = field.substring(8)

            if (vatom.private?.has(privateField) == true) {
              if (vatom.private?.get(privateField) == policy.get("value")) {
                return vatom.property.getResource(policy.getString("resource"))
              }
            }
          } else {
            ///not supported
          }
        } else {
          return vatom.property.getResource(policy.getString("resource"))
        }
      }
    }
    return vatom.property.getResource("ActivatedImage")
  }

  class Config(val policy: List<JSONObject>, val scale: String) {

    constructor(json: JSONObject) : this({
      val list = json.getJSONArray("image_policy")
      val policy = ArrayList<JSONObject>()
      (0 until list.length()).forEach { policy.add(list.getJSONObject(it)) }
      policy
    }.invoke(), json.optString("scale", "fit"))
  }

  companion object {

    internal val countCache = LruCache<String, Int>(100)

    val factory: ViewFactory
      get() {
        return ViewFactory.wrap("native://image-policy") { vatom, face, bridge ->
          ImagePolicyFace(vatom, face, bridge)
        }
      }

  }
}