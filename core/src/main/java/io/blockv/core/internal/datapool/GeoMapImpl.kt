package io.blockv.core.internal.datapool

import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.api.VatomApi
import io.blockv.common.internal.net.rest.request.GeoRequest
import io.blockv.common.internal.net.websocket.Websocket
import io.blockv.common.internal.net.websocket.request.BaseRequest
import io.blockv.common.internal.net.websocket.request.MonitorRequest
import io.blockv.common.model.Message
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.Vatom
import io.blockv.common.model.WebSocketEvent
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
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class GeoMapImpl(
  val vatomApi: VatomApi,
  val webSocket: Websocket,
  val jsonModule: JsonModule
) : GeoMap {
  var bottomLeftLat: Double = 0.0
  var bottomLeftLon: Double = 0.0
  var topRightLat: Double = 0.0
  var topRightLon: Double = 0.0
  val vatoms = HashMap<String, Vatom>()
  val brains = HashMap<String, Brain>()
  var emitter: FlowableEmitter<Message<Vatom>>? = null
  var flowable: Flowable<Message<Vatom>>? = null
  val disposable = CompositeDisposable()
  var brainDisposable: Disposable? = null
  val brainUpdater = Observable.interval(1000 / 15, TimeUnit.MILLISECONDS)
    .observeOn(Schedulers.computation())
    .map {
      synchronized(vatoms)
      {
        synchronized(brains)
        {
          val updates = ArrayList<Vatom>()
          brains.keys.forEach {
            val vatom = vatoms[it]
            val brain = brains[it]
            if (vatom != null && brain != null) {
              brain.path = brain.filter(brain.path)
              val current = Date().time
              val startPos = ArrayList(vatom.property.geoPos!!.coordinates)
              if (brain.path.isNotEmpty()) {
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
                updates.add(vatom)
              }
            }

          }
          if (emitter?.isCancelled == false && updates.isNotEmpty()) {
            emitter?.onNext(Message(updates, Message.Type.UPDATED, Message.State.STABLE))
          }
          if (updates.isEmpty()) {
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
  ): Flowable<Message<Vatom>> {
    return Flowable.create<Message<Vatom>>({ emitter ->
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
          flowable = Flowable.create<Message<Vatom>>({ emitter ->
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
            emitter.onNext(Message(vatoms.values.toList(), Message.Type.INITIAL, Message.State.UNSTABLE))

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
                    emitter.onNext(Message(vatoms.values.toList(), Message.Type.INITIAL, Message.State.STABLE))
                  }
                }, { emitter.onError(it) })
            )

            disposable.add(
              getBrainUpdates(
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
        emitter.onNext(Message(vatoms.values.toList(), Message.Type.INITIAL, Message.State.STABLE))
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


  fun getBrainUpdates(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double
  ): Flowable<Message<Vatom>> {

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
        it.type == WebSocketEvent.MessageType.STATE_UPDATE && it.payload?.optString("action_name") == "brain-update"
      }
      .observeOn(Schedulers.computation())
      .map {
        if (it.payload != null) {
          Optional(jsonModule.deserialize<StateUpdateEvent>(it.payload!!))
        } else
          Optional(null)
      }
      .filter { !it.isEmpty() }
      .map { it.value!! }
      .map {
        if (it.vatomProperties
            .optJSONObject("vAtom::vAtomType")
            ?.optBoolean("dropped", true) == false
        ) {
          synchronized(brains)
          {
            brains.remove(it.vatomId)
          }
          synchronized(vatoms)
          {
            val vatom = vatoms.remove(it.vatomId)
            if (vatom != null) {
              Optional(Message(vatom, Message.Type.REMOVED, Message.State.STABLE))
            } else
              Optional(null)
          }
        } else
          if (it.vatomProperties.has("next_positions")) {
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
            Optional(null)
          } else
            Optional(null)
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