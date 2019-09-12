package io.blockv.core.internal.datapool

import io.blockv.common.model.Vatom
import io.reactivex.Flowable

interface GeoMap {

  fun getRegion(
    bottomLeftLat: Double,
    bottomLeftLon: Double,
    topRightLat: Double,
    topRightLon: Double
  ): Flowable<List<Vatom>>
}