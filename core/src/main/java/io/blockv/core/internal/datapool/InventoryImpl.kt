package io.blockv.core.internal.datapool

import android.database.DatabaseUtils
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import androidx.sqlite.db.SimpleSQLiteQuery
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.api.VatomApi
import io.blockv.common.internal.net.rest.request.InventoryRequest
import io.blockv.common.internal.net.rest.request.InventorySyncRequest
import io.blockv.common.internal.net.rest.request.VatomRequest
import io.blockv.common.internal.net.websocket.Websocket
import io.blockv.common.internal.repository.Preferences
import io.blockv.common.model.InventoryEvent
import io.blockv.common.model.InventorySync
import io.blockv.common.model.Pack
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.Vatom
import io.blockv.common.model.VatomGroup
import io.blockv.common.model.WebSocketEvent
import io.blockv.common.util.JsonUtil
import io.blockv.common.util.Optional
import io.blockv.core.client.manager.VatomManager
import io.blockv.core.internal.repository.Database
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class InventoryImpl(
  val vatomApi: VatomApi,
  val webSocket: Websocket,
  val jsonModule: JsonModule,
  val database: Database,
  val preferences: Preferences
) : Inventory {

  var state = VatomManager.CacheState.DISPOSED
    set(value) {
      field = value
      if (stateEmitter?.isCancelled == false) stateEmitter?.onNext(value)
    }
  var stateEmitter: FlowableEmitter<VatomManager.CacheState>? = null
  val stateFlowable = Flowable.create<VatomManager.CacheState>({
    stateEmitter = it
    it.setCancellable {
      stateEmitter = null
    }
  }, BackpressureStrategy.LATEST)
    .share()

  var inventoryEmitter: ObservableEmitter<Unit>? = null
  val inventory =
    Observable.create<Unit> { emitter ->
      inventoryEmitter = emitter
      val disposable = CompositeDisposable()
      emitter.setDisposable(disposable)
      val messages = ArrayList<WebSocketEvent<JSONObject>>()
      val lock = Object()
      val processMessage: (message: WebSocketEvent<JSONObject>) -> Unit =
        { message ->
          synchronized(lock) {
            try {
              if (message.type == WebSocketEvent.MessageType.INVENTORY) {
                val event = jsonModule.deserialize<InventoryEvent>(message.payload!!)
                val dbVatoms = database.vatomDao().getVatoms(listOf(event.vatomId)).blockingFirst()
                if (dbVatoms.isEmpty() || dbVatoms.first().vatom == null) {
                  vatomApi.getUserVatom(VatomRequest(listOf(event.vatomId))).payload
                    .forEach { vatom ->
                      database.vatomDao().addOrUpdateFaces(vatom.faces).blockingGet()
                      database.vatomDao().addOrUpdateActions(vatom.actions).blockingGet()
                      database.vatomDao().addOrUpdateVatoms(listOf(vatom)).blockingGet()
                    }

                } else
                  if (dbVatoms.first().vatom?.property?.owner != event.newOwnerId) {
                    database.vatomDao().removeVatom(listOf(dbVatoms.first().vatom).filterNotNull()).blockingGet()
                  }
              } else
                if (message.type == WebSocketEvent.MessageType.STATE_UPDATE) {
                  val event = jsonModule.deserialize<StateUpdateEvent>(message.payload!!)
                  if (event.operation.toLowerCase() == "update") {
                    val dbVatoms = database.vatomDao().getVatoms(listOf(event.vatomId)).blockingFirst()
                    if (dbVatoms.isNotEmpty() && dbVatoms.first().vatom != null
                      && (
                        event.vatomProperties
                          .optJSONObject("vAtom::vAtomType")
                          ?.optString("owner", null) == null
                          || event.vatomProperties
                          .optJSONObject("vAtom::vAtomType")
                          ?.optString("owner") == dbVatoms.first().vatom?.property?.owner)
                    ) {
                      val json = jsonModule.serialize(dbVatoms.first().vatom!!)!!
                      JsonUtil.merge(json, event.vatomProperties)
                      val vatom = jsonModule.deserialize<Vatom>(json)
                      database.vatomDao().addOrUpdateVatoms(listOf(vatom)).blockingGet()
                    }
                  }
                }
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }
        }
      state = VatomManager.CacheState.UNSTABLE

      val timer = Flowable.timer(300, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.computation())
        .doFinally {
          disposable.add(
            getInventoryHash()
              .flatMap { hash ->
                if (hash == preferences.inventoryHash) {
                  Observable.fromCallable {
                    synchronized(this) {
                      messages.forEach { processMessage(it) }
                      state = VatomManager.CacheState.STABLE
                    }
                  }
                } else {
                  getInventorySync()
                    .map { Pair(hash, it) }
                    .flatMap { state ->
                      database.vatomDao()
                        .getVatomSync()
                        .subscribeOn(Schedulers.io())
                        .toObservable()
                        .map { vatomSync ->
                          val ids = ArrayList<String>()
                          val remove = vatomSync
                            .map { it.id }
                            .toMutableList()

                          state.second.forEach {
                            val sync = vatomSync.find { sync -> it.id == sync.id }
                            if (sync == null || sync.sync != it.sync) {
                              ids.add(it.id)
                            }
                            remove.remove(it.id)
                          }

                          Triple(state.first, ids, remove)
                        }
                    }
                    .flatMap {
                      if (it.third.isNotEmpty()) {
                        database.vatomDao().removeVatomById(it.third).blockingGet()
                      }
                      if (it.second.isNotEmpty()) {
                        fetchVatoms(it.second)
                          .observeOn(Schedulers.io())
                          .map {
                            database.vatomDao().addOrUpdateFaces(it.faces).blockingGet()
                            database.vatomDao().addOrUpdateActions(it.actions).blockingGet()
                            database.vatomDao().addOrUpdateVatoms(it.vatoms).blockingGet()
                          }
                      } else {
                        Observable.just(Unit)
                      }
                        .doOnComplete {
                          preferences.inventoryHash = it.first
                          synchronized(this) {
                            messages.forEach { processMessage(it) }
                            state = VatomManager.CacheState.STABLE
                          }
                        }
                    }
                }
              }
              .subscribe({
              }, {
                state = VatomManager.CacheState.UNSTABLE
                it.printStackTrace()
                var first = true
                disposable.add(
                  fetchInventory()
                    .observeOn(Schedulers.io())
                    .flatMap { pack ->
                      if (first) {
                        first = false
                        database.vatomDao().removeAll()
                          .flatMap {
                            database.vatomDao()
                              .addOrUpdateFaces(pack.faces)
                              .observeOn(Schedulers.io())
                              .flatMap {
                                database.vatomDao()
                                  .addOrUpdateActions(pack.actions)
                              }
                              .observeOn(Schedulers.io())
                              .flatMap {
                                database.vatomDao()
                                  .addOrUpdateVatoms(pack.vatoms)
                              }
                          }
                          .toObservable()
                      } else
                        database.vatomDao()
                          .addOrUpdateFaces(pack.faces)
                          .observeOn(Schedulers.io())
                          .flatMap {
                            database.vatomDao()
                              .addOrUpdateActions(pack.actions)
                          }
                          .observeOn(Schedulers.io())
                          .flatMap {
                            database.vatomDao()
                              .addOrUpdateVatoms(pack.vatoms)
                          }
                          .toObservable()
                    }
                    .doOnComplete {
                      synchronized(this) {
                        messages.forEach { processMessage(it) }
                        state = VatomManager.CacheState.STABLE
                      }
                    }
                    .subscribe({

                    }, {
                      emitter.onError(it)
                    })
                )
              })
          )
        }
        .subscribe()

      disposable.add(timer)

      disposable.add(webSocket
        .connect()
        .doOnNext(object : Consumer<WebSocketEvent<JSONObject>> {
          var first = true
          override fun accept(t: WebSocketEvent<JSONObject>?) {
            if (first) {
              timer.dispose()
            }
          }
        })
        .filter {
          it.payload != null
            && (it.type == WebSocketEvent.MessageType.STATE_UPDATE
            || it.type == WebSocketEvent.MessageType.INVENTORY)
        }
        .observeOn(Schedulers.io())
        .subscribe({ message ->
          synchronized(this) {
            if (state == VatomManager.CacheState.UNSTABLE) {
              messages.add(message)
            } else {
              processMessage(message)
            }
          }
        }, {
          emitter.onError(it)
        })
      )

    }
      .subscribeOn(Schedulers.io())
      .doOnError {
        it.printStackTrace()
        synchronized(this)
        {
          state = VatomManager.CacheState.UNSTABLE
        }
      }
      .retryWhen { errors ->
        errors.flatMap { error ->
          if (error is DatapoolException) {
            if (error.error == DatapoolException.Error.REGION_DISPOSED) {
              Observable.error(error)
            } else
              Observable.just(error)
                .delay(300, TimeUnit.MILLISECONDS)
          } else
            Observable.just(error)
              .delay(3, TimeUnit.SECONDS)
        }
      }
      .share()

  override fun getRegion(
    id: String,
    orderBy: VatomManager.SortOrder,
    category: String,
    search: String,
    count: Int,
    group: Boolean,
    initialIndex: Int
  ): Flowable<PagedList<VatomGroup>> {

    return queryVatoms(null, id, orderBy, category, search, count, group, initialIndex)
  }

  fun queryVatoms(
    ids: List<String>?,
    parentId: String?,
    orderBy: VatomManager.SortOrder,
    category: String,
    search: String,
    count: Int,
    group: Boolean,
    initialIndex: Int
  ): Flowable<PagedList<VatomGroup>> {

    val query = if (group)
      SimpleSQLiteQuery(
        "SELECT *, COUNT(templateVariationId) as count FROM " +
          "( SELECT * FROM vatom " +
          if (ids?.isNotEmpty() != true) {
            "WHERE parentId = '$parentId' "
          } else {
            "WHERE id IN (${ids.map { "\'$it\'" }.joinToString(",")}) "
          } +
          "AND isDropped = 0 " +
          "AND templateId NOT LIKE '%::vAtom::Avatar' " +
          "AND templateId NOT LIKE '::vAtom::CoinWallet' " +
          (if (category.isNotEmpty()) "AND category = ${DatabaseUtils.sqlEscapeString(category)} COLLATE NOCASE " else "") +
          (if (search.isNotEmpty()) "AND title LIKE '%${escapeSql(search)}%' ESCAPE '\\' COLLATE NOCASE " else "") +
          "ORDER BY whenModified ASC ) " +
          "GROUP BY templateVariationId " +
          "ORDER BY ${when (orderBy) {
            VatomManager.SortOrder.ADDED_DESC -> "whenAdded DESC"
            VatomManager.SortOrder.ADDED_ASC -> "whenAdded ASC"
            VatomManager.SortOrder.UPDATED_DESC -> "whenModified DESC"
            VatomManager.SortOrder.UPDATED_ASC -> "whenModified ASC"
            VatomManager.SortOrder.TITLE_ASC -> "title COLLATE NOCASE ASC"
            VatomManager.SortOrder.TITLE_DESC -> "title COLLATE NOCASE DESC"
          }} " +
          if (count > 0) "LIMIT $count" else ""
      )
    else
      SimpleSQLiteQuery(
        "SELECT * FROM vatom " +
          if (ids?.isNotEmpty() != true) {
            "WHERE parentId = '$parentId' "
          } else {
            "WHERE id IN (${ids.map { "\'$it\'" }.joinToString(",")}) "
          } +
          "AND isDropped = 0 " +
          "AND templateId NOT LIKE '%::vAtom::Avatar' " +
          "AND templateId NOT LIKE '::vAtom::CoinWallet' " +
          (if (category.isNotEmpty()) "AND category = ${DatabaseUtils.sqlEscapeString(category)} COLLATE NOCASE " else "") +
          (if (search.isNotEmpty()) "AND title LIKE '%${escapeSql(search)}%' ESCAPE '\\' COLLATE NOCASE " else "") +
          "ORDER BY ${when (orderBy) {
            VatomManager.SortOrder.ADDED_DESC -> "whenAdded DESC"
            VatomManager.SortOrder.ADDED_ASC -> "whenAdded ASC"
            VatomManager.SortOrder.UPDATED_DESC -> "whenModified DESC"
            VatomManager.SortOrder.UPDATED_ASC -> "whenModified ASC"
            VatomManager.SortOrder.TITLE_ASC -> "title COLLATE NOCASE ASC"
            VatomManager.SortOrder.TITLE_DESC -> "title COLLATE NOCASE DESC"
          }} " +
          if (count > 0) "LIMIT $count" else ""
      )

    return Flowable.create<PagedList<VatomGroup>>({ emitter ->
      val disposable = CompositeDisposable()
      disposable.add(RxPagedListBuilder<Int, VatomGroup>(
        database
          .vatomDao()
          .getVatoms(query)
          .mapByPage { page ->
            page
              .filter {
                it.vatom != null
              }
              .map {
                it.vatom?.faces = it.faces ?: emptyList()
                it.vatom?.actions = it.actions ?: emptyList()
                VatomGroup(it.count, it.vatom!!)
              }
          }
        , 50
      )
        .setInitialLoadKey(initialIndex)
        .buildFlowable(BackpressureStrategy.BUFFER)
        .subscribe({
          emitter.onNext(it)
        }, {
          emitter.onError(it)
        })
      )
      disposable.add(inventory.subscribe({}, { error ->
        if (error is DatapoolException && error.error == DatapoolException.Error.REGION_DISPOSED) {
          emitter.onError(error)
        }
      }))
      inventory.subscribe({}, {})
      emitter.setDisposable(disposable)
    }, BackpressureStrategy.BUFFER)
  }

  override fun getVatoms(
    ids: List<String>,
    orderBy: VatomManager.SortOrder,
    search: String,
    count: Int,
    group: Boolean,
    initialIndex: Int
  ): Flowable<PagedList<VatomGroup>> {
    return queryVatoms(ids, null, orderBy, "", search, count, group, initialIndex)
  }

  fun escapeSql(sql: String): String {
    return sql
      .replace("//", "///")
      .replace("'", "//'")
      .replace("%", "//%")
      .replace("%", "//%")
      .replace("_", "//_")

  }

  override fun getVatom(id: String): Flowable<Pair<VatomManager.CacheState, Vatom?>> {

    return Flowable.create<Pair<VatomManager.CacheState, Vatom?>>({ emitter ->
      val disposable = CompositeDisposable()
      emitter.setDisposable(disposable)
      var internalVatom: Vatom? = null
      var internalState: VatomManager.CacheState? = null
      disposable.add(
        Flowable.combineLatest<VatomManager.CacheState, Optional<Vatom>, Pair<VatomManager.CacheState, Vatom?>>(
          getState(),
          database
            .vatomDao()
            .getVatoms(listOf(id))
            .map {
              Optional(
                if (it.isNotEmpty()) {
                  val pack = it.first()
                  pack.vatom?.actions = pack.actions ?: emptyList()
                  pack.vatom?.faces = pack.faces ?: emptyList()
                  pack.vatom
                } else null
              )
            },
          BiFunction
          { state, vatom ->
            Pair(state, vatom.value)
          })
          .subscribeOn(Schedulers.io())
          .subscribe({
            if (!emitter.isCancelled) {
              if (
                internalState != it.first
                || it.second == null
                || internalVatom == null
                || it.second?.whenModified != internalVatom?.whenModified
              ) {
                internalState = it.first
                internalVatom = it.second
                emitter.onNext(it)
              }
            }
          }, {
            if (!emitter.isCancelled) {
              emitter.onError(it)
            }
          })
      )
    }, BackpressureStrategy.BUFFER)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun fetchInventory(): Observable<Pack> {
    return Observable.create<Pack> { emitter ->
      var page = 1
      do {
        val result = vatomApi.getInventoryJson(InventoryRequest("*", page, 100))
        val pack = jsonModule.deserialize<Pack>(result.payload)
        page += 1
        emitter.onNext(pack)
      } while (pack.vatoms.isNotEmpty()
        && !emitter.isDisposed
      )
      emitter.onComplete()
    }.subscribeOn(Schedulers.io())
  }

  fun fetchVatoms(ids: List<String>): Observable<Pack> {
    return Observable.create<Pack> { emitter ->
      var page = 0
      do {
        val list = ids.subList(page * 100, Math.min(page * 100 + 100, ids.size))
        val result = vatomApi.getVatomJson(VatomRequest(list))
        val pack = jsonModule.deserialize<Pack>(result.payload)
        page += 1
        emitter.onNext(pack)
      } while (
        list.size == 100
        && pack.vatoms.isNotEmpty()
        && !emitter.isDisposed
      )
      emitter.onComplete()
    }.subscribeOn(Schedulers.io())
  }

  fun getInventoryHash(): Observable<String> {
    return Observable.fromCallable {
      vatomApi.getInventoryHash().payload
    }
      .subscribeOn(Schedulers.io())
  }

  fun getInventorySync(): Observable<List<InventorySync.VatomSync>> {
    return Observable.fromCallable {
      val list = ArrayList<InventorySync.VatomSync>()
      var token = ""
      do {
        val payload = vatomApi.getInventorySync(InventorySyncRequest(500, token)).payload
        list.addAll(payload.vatoms)
        token = payload.token
      } while (payload.vatoms.size == 500)
      list.toList()
    }
      .subscribeOn(Schedulers.io())
  }

  override fun dispose() {
    if (inventoryEmitter?.isDisposed == false) {
      inventoryEmitter?.onError(DatapoolException.Error.REGION_DISPOSED.exception())
    }
    state = VatomManager.CacheState.DISPOSED
  }

  override fun performAction(action: String, payload: JSONObject): Observable<Unit> {
    return Observable.fromCallable {
      payload.optString("this.id") ?: ""
    }
      .filter { it.isNotEmpty() }
      .flatMap {
        database.vatomDao().getVatoms(listOf(it))
          .firstOrError()
          .toObservable()
      }
      .filter { it.isNotEmpty() && it.first().vatom != null }
      .map { it.first().vatom!! }
      .flatMapSingle<Unit> { vatom ->
        when (action.toLowerCase()) {
          "transfer", "redeem", "trash" -> {
            database.vatomDao()
              .removeVatom(listOf(vatom))
              .map { Unit }
          }
          "drop" -> {
            vatom.property.isDropped = true
            database.vatomDao()
              .addOrUpdateVatoms(listOf(vatom))
              .map { Unit }
          }
          else -> Single.just(Unit)
        }
      }
      .subscribeOn(Schedulers.io())
  }

  override fun setParentId(ids: Map<String, String>): Single<Map<String, String>> {
    return database.vatomDao()
      .getVatoms(ids.keys.toList())
      .firstElement()
      .filter { it.isNotEmpty() }
      .map { it.map { it.vatom }.filterNotNull() }
      .flatMap { vatoms ->
        val oldId = HashMap<String, String>()
        vatoms.forEach {
          oldId[it.id] = it.property.parentId ?: "."
          it.property.parentId = ids[it.id] ?: "."
        }
        database.vatomDao()
          .addOrUpdateVatoms(vatoms)
          .toMaybe()
          .map { oldId.toMap() }
      }
      .toSingle(HashMap())
      .subscribeOn(Schedulers.io())
  }

  override fun invalidate() {
    if (inventoryEmitter?.isDisposed == false) {
      inventoryEmitter?.onError(DatapoolException.Error.REGION_INVALIDATED.exception())
    }
    state = VatomManager.CacheState.UNSTABLE
  }

  override fun getCategories(): Single<List<String>> {
    return database.vatomDao()
      .getCategories()
      .subscribeOn(Schedulers.io())
  }

  override fun getState(): Flowable<VatomManager.CacheState> {
    return Single.fromCallable {
      state
    }.toFlowable()
      .mergeWith(stateFlowable)
  }

  override fun clear(): Single<Unit> {
    return Single.fromCallable {
      dispose()
    }
      .flatMap {
        preferences.inventoryHash = null
        database.vatomDao().removeAll()
      }
      .subscribeOn(Schedulers.io())
      .map {}
  }
}