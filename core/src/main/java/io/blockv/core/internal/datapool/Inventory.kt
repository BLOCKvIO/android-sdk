package io.blockv.core.internal.datapool

import io.blockv.common.model.Message
import io.blockv.common.model.Vatom
import io.reactivex.Flowable
import io.reactivex.Single

interface Inventory {

  fun getRegion(id: String = "."): Flowable<Message<Vatom>>

  fun getVatom(id: String): Flowable<Message<Vatom>>

  fun invalidate()

  fun dispose()

  fun reset(): Single<Unit>

}