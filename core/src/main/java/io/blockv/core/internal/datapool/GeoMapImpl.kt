package io.blockv.core.internal.datapool

import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.api.VatomApi
import io.blockv.common.internal.net.rest.request.GeoRequest
import io.blockv.common.internal.net.rest.request.VatomRequest
import io.blockv.common.internal.net.websocket.Websocket
import io.blockv.common.internal.net.websocket.request.BaseRequest
import io.blockv.common.internal.net.websocket.request.MonitorRequest
import io.blockv.common.model.MapEvent
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.Vatom
import io.blockv.common.model.WebSocketEvent
import io.blockv.common.util.JsonUtil
import io.blockv.common.util.Optional
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class GeoMapImpl(
  val vatomApi: VatomApi,
  val webSocket: Websocket,
  val jsonModule: JsonModule
) : GeoMap {
  var isUpdated = false
  var bottomLeftLat: Double = 0.0
  var bottomLeftLon: Double = 0.0
  var topRightLat: Double = 0.0
  var topRightLon: Double = 0.0
  val vatoms = HashMap<String, Vatom>()
  val brains = HashMap<String, Brain>()
  var emitter: FlowableEmitter<List<Vatom>>? = null
  var flowable: Flowable<List<Vatom>>? = null
  val disposable = CompositeDisposable()
  var brainDisposable: Disposable? = null
  val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
  val brainUpdater = Observable.interval(1000 / 15, TimeUnit.MILLISECONDS)
    .observeOn(Schedulers.computation())
    .map {
      synchronized(vatoms)
      {
        synchronized(brains)
        {
          brains.keys.forEach {
            val vatom = vatoms[it]
            val brain = brains[it]
            if (vatom != null && brain != null) {
              brain.path = brain.filter(brain.path)
              val current = Date().time
              val startPos = ArrayList(vatom.property.geoPos!!.coordinates)
              if (brain.path.isNotEmpty()) {
                isUpdated = true
                val endPos = brain.path[0]
                val remainingTime = endPos.time - current

                var interval = (remainingTime * 15f / 1000)

                if (interval < 1) {
                  interval = 1f
                }
                val lon = (endPos.lon - startPos[1]) / interval
                val lat = (endPos.lat - startPos[0]) / interval
                startPos[0] += lat
                startPos[1] += lon

                vatom.property.geoPos?.coordinates = startPos
                val time = dateFormatter.format(Date(System.currentTimeMillis()))
                val newVatom = Vatom(
                  vatom.id,
                  vatom.whenCreated,
                  time,
                  vatom.property,
                  vatom.private,
                  vatom.sync
                )
                newVatom.faces = vatom.faces
                newVatom.actions = vatom.actions
                vatoms[vatom.id] = newVatom
              }
            }

          }
          if (emitter?.isCancelled == false && isUpdated) {
            emitter?.onNext(vatoms.values.toList())
            isUpdated = false
          }
          if (isUpdated) {
            brainDisposable?.dispose()
            brainDisposable = null
          }
        }
      }

    }
    .retryWhen { it }

  override fun getRegion(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double
  ): Flowable<List<Vatom>> {
    return Flowable.create<List<Vatom>>({ emitter ->
      synchronized(vatoms) {
        if (this.bottomLeftLat != bottomLeftLat
          || this.bottomLeftLon != bottomLeftLon
          || this.topRightLat != topRightLat
          || this.topRightLon != topRightLon
        ) {
          if (this.emitter?.isCancelled == false) {
            this.emitter?.onError(DatapoolException.Error.REGION_DISPOSED.exception())
          }
          flowable = null
        }
        if (flowable == null) {
          flowable = Flowable.create<List<Vatom>>({ emitter ->
            this.emitter = emitter
            val disposable = CompositeDisposable()
            emitter.setDisposable(disposable)
            vatoms.values.toList().forEach {
              val pos = it.property.geoPos
              if (pos?.coordinates == null
                || pos.coordinates!!.size < 2
                || pos.coordinates!![0] < bottomLeftLon
                || pos.coordinates!![0] > topRightLon
                || pos.coordinates!![1] < bottomLeftLat
                || pos.coordinates!![1] > topRightLat
              ) {
                vatoms.remove(it.id)
              }
            }
            emitter.onNext(vatoms.values.toList())

            disposable.add(
              Single.fromCallable {
                vatomApi
                  .geoDiscover(
                    GeoRequest(
                      bottomLeftLon,
                      bottomLeftLat,
                      topRightLon,
                      topRightLat,
                      "vatoms"
                    )
                  ).payload
              }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe({
                  synchronized(vatoms) {
                    val old = HashMap(vatoms)
                    vatoms.clear()
                    it.forEach {
                      if (brains.containsKey(it.id) && old.containsKey(it.id)) {
                        it.property.geoPos = old[it.id]!!.property.geoPos
                      }
                      vatoms[it.id] = it
                    }
                    emitter.onNext(vatoms.values.toList())
                  }
                }, { emitter.onError(it) })
            )

            disposable.add(
              getMapUpdates(
                bottomLeftLat,
                bottomLeftLon,
                topRightLat,
                topRightLon
              )
                .subscribe({
                  emitter.onNext(it)
                }, {
                  it.printStackTrace()
                })
            )
          }, BackpressureStrategy.BUFFER)
            .subscribeOn(Schedulers.io())
            .share()
        }
        emitter.onNext(vatoms.values.toList())
        emitter.setDisposable(flowable!!.subscribe({
          emitter.onNext(it)
        }, {
          emitter.onError(it)
        }))

      }
    }, BackpressureStrategy.BUFFER)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun getMapUpdates(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double
  ): Flowable<List<Vatom>> {

    return webSocket
      .connectAndMessage(
        BaseRequest(
          command = "monitor",
          payload = MonitorRequest(
            bottomLeftLat,
            bottomLeftLon,
            topRightLat,
            topRightLon
          )
        )
      )
      .observeOn(Schedulers.io())
      .filter {
        (it.type == WebSocketEvent.MessageType.STATE_UPDATE
          || it.type == WebSocketEvent.MessageType.MAP)
      }
      .observeOn(Schedulers.computation())
      .flatMap { event ->
        if (event.type == WebSocketEvent.MessageType.STATE_UPDATE) {
          Flowable.fromCallable {
            if (event.payload != null) {
              Optional(jsonModule.deserialize<StateUpdateEvent>(event.payload!!))
            } else
              Optional(null)
          }
            .subscribeOn(Schedulers.computation())
            .filter { !it.isEmpty() }
            .map { it.value!! }
            .flatMap {
              when {
                it.vatomProperties
                  .optJSONObject("vAtom::vAtomType")
                  ?.optBoolean("dropped", true) == false -> {
                  synchronized(brains)
                  {
                    brains.remove(it.vatomId)
                  }
                  synchronized(vatoms)
                  {
                    val vatom = vatoms.remove(it.vatomId)
                    if (vatom != null) {
                      Flowable.just(Optional(vatoms.values.toList()))
                    } else
                      Flowable.just(Optional(null))
                  }
                }
                it.vatomProperties
                  .optJSONObject("vAtom::vAtomType")
                  ?.optBoolean("dropped", false) == true -> {
                  Flowable.fromCallable {
                    val list = vatomApi.getUserVatom(VatomRequest(listOf(it.vatomId)))
                      .payload
                    if (list.isNotEmpty()) {
                      synchronized(vatoms)
                      {
                        vatoms[it.vatomId] = list[0]
                        Optional(vatoms.values.toList())
                      }
                    } else
                      Optional(null)
                  }.subscribeOn(Schedulers.io())
                    .onErrorReturn {
                      Optional(null)
                    }
                }
                it.vatomProperties.has("next_positions") -> {
                  synchronized(brains)
                  {
                    val positions = it.vatomProperties.getJSONArray("next_positions")
                    val path = (0 until positions.length()).map {
                      val pos = positions.getJSONObject(it)
                      val time = pos.getLong("time")
                      val geoPos = pos.getJSONArray("geo_pos")
                      Position(time, geoPos.getDouble(0).toFloat(), geoPos.getDouble(1).toFloat())
                    }
                    if (brains.containsKey(it.vatomId)) {
                      brains[it.vatomId]!!.updatePath(path)

                    } else {
                      brains[it.vatomId] = Brain(it.vatomId, ArrayList(path))
                    }
                    if (brainDisposable?.isDisposed != false) {
                      brainDisposable = brainUpdater.subscribe({
                      }, {})
                      disposable.add(brainDisposable!!)
                    }

                  }
                  Flowable.just(Optional(null))
                }
                else -> Flowable.fromCallable {
                  synchronized(vatoms)
                  {
                    val vatom = vatoms[it.vatomId]
                    if (vatom != null) {
                      val json = jsonModule.serialize(vatom)!!
                      JsonUtil.merge(json, it.vatomProperties)
                      vatoms[it.vatomId] = jsonModule.deserialize(json)
                      Optional(vatoms.values.toList())
                    } else
                      Optional(null)
                  }
                }.subscribeOn(Schedulers.computation())
                  .onErrorReturn { Optional(null) }
              }
            }
        } else {
          Flowable.fromCallable {
            if (event.payload != null) {
              Optional(jsonModule.deserialize<MapEvent>(event.payload!!))
            } else
              Optional(null)
          }
            .subscribeOn(Schedulers.computation())
            .filter { !it.isEmpty() }
            .map { it.value!! }
            .flatMap { event ->
              when {
                event.operation.equals("add", true) -> {
                  Flowable.fromCallable {
                    val list = vatomApi.getUserVatom(VatomRequest(listOf(event.vatomId)))
                      .payload
                    if (list.isNotEmpty()) {
                      synchronized(vatoms)
                      {
                        vatoms[event.vatomId] = list[0]
                        Optional(vatoms.values.toList())
                      }
                    } else
                      Optional(null)
                  }.subscribeOn(Schedulers.io())
                    .onErrorReturn {
                      Optional(null)
                    }
                }
                event.operation.equals("remove", true) -> {
                  Flowable.fromCallable {
                    synchronized(vatoms)
                    {
                      val vatom = vatoms.remove(event.vatomId)
                      if (vatom != null) {
                        Optional(vatoms.values.toList())
                      } else
                        Optional(null)
                    }
                  }
                }
                else -> {
                  Flowable.just(Optional(null))
                }
              }
            }
        }
      }
      .filter { !it.isEmpty() }
      .map { it.value!! }
      .doOnError { it.printStackTrace() }
      .retryWhen { it.delay(3, TimeUnit.SECONDS) }

  }

  inner class Brain(
    var vatomId: String,
    var path: MutableList<Position>
  ) {

    @Synchronized
    fun updatePath(path: List<Position>) {
      val sorted = ArrayList(path.sortedBy { it.time })
      if (this.path.isEmpty()) {
        this.path = sorted
      } else {
        val positionFirst = this.path[0]
        var index = 0
        for (position in sorted) {
          if (position.time > positionFirst.time) {
            break
          }
          index++
        }
        if (index < sorted.size) {
          val out = sorted.subList(index, sorted.size)
          this.path = out
        } else {
          this.path = sorted
        }
      }
    }

    @Synchronized
    fun filter(path: List<Position>): ArrayList<Position> {
      val current = Date().time
      val out = path.filter {
        it.time >= current
      }
      return ArrayList(out)
    }
  }

  class Position(val time: Long, val lat: Float, val lon: Float)
}