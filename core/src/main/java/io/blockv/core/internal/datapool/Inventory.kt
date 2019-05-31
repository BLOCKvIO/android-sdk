package io.blockv.core.internal.datapool

import io.blockv.common.model.Message
import io.blockv.common.model.Vatom
import io.reactivex.Completable
import io.reactivex.Flowable

interface Inventory {

  fun getRegion(id: String = "."): Flowable<Message<Vatom>>

  fun getVatom(id: String): Flowable<Message<Vatom>>

  fun dispose()

  fun reset(): Completable

}