package io.blockv.core.internal.datapool

import io.blockv.common.internal.net.rest.api.VatomApi
import io.blockv.common.internal.net.rest.request.GeoRequest
import io.blockv.common.internal.net.websocket.Websocket
import io.blockv.common.model.Message
import io.blockv.common.model.Vatom
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GeoMapImpl(
  val vatomApi: VatomApi,
  val webSocket: Websocket
) : GeoMap {
  var bottomLeftLat: Double = 0.0
  var bottomLeftLon: Double = 0.0
  var topRightLat: Double = 0.0
  var topRightLon: Double = 0.0
  val vatoms = HashMap<String, Vatom>()
  var emitter: FlowableEmitter<Message<Vatom>>? = null
  var flowable: Flowable<Message<Vatom>>? = null

  override fun getRegion(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double
  ): Flowable<Message<Vatom>> {
    return Flowable.create<Message<Vatom>>({ emitter ->
      synchronized(this) {
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
                      topRightLat, "vatoms"
                    )
                  ).payload
              }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe({
                  vatoms.clear()
                  it.forEach {
                    vatoms[it.id] = it
                  }
                  emitter.onNext(Message(vatoms.values.toList(), Message.Type.INITIAL, Message.State.STABLE))
                }, { emitter.onError(it) })
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


}