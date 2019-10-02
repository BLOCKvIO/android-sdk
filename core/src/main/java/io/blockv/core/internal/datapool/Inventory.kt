package io.blockv.core.internal.datapool

import androidx.paging.PagedList
import io.blockv.common.model.Vatom
import io.blockv.common.model.VatomGroup
import io.blockv.core.client.manager.VatomManager
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONObject

interface Inventory {

  fun getRegion(
    id: String = ".",
    orderBy: VatomManager.SortOrder,
    category: String = "",
    search: String = "",
    count: Int = 0,
    group: Boolean = false,
    initialIndex: Int = 0
  ): Flowable<PagedList<VatomGroup>>

  fun getVatom(id: String): Flowable<Pair<VatomManager.CacheState, Vatom?>>

  fun getVatoms(
    ids: List<String>,
    orderBy: VatomManager.SortOrder,
    search: String = "",
    count: Int = 0,
    group: Boolean = false,
    initialIndex: Int = 0
  ): Flowable<PagedList<VatomGroup>>

  fun invalidate()

  fun dispose()

  fun clear(): Single<Unit>

  fun performAction(action: String, payload: JSONObject): Observable<Unit>

  fun setParentId(ids: Map<String, String>): Single<Map<String, String>>

  fun getCategories(): Single<List<String>>

  fun getState(): Flowable<VatomManager.CacheState>
}