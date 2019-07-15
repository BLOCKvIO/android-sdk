package io.blockv.core.internal.datapool

import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.api.VatomApi
import io.blockv.common.internal.net.rest.request.InventoryRequest
import io.blockv.common.internal.net.rest.request.VatomRequest
import io.blockv.common.internal.net.websocket.Websocket
import io.blockv.common.internal.repository.Database
import io.blockv.common.model.Action
import io.blockv.common.model.Face
import io.blockv.common.model.InventoryEvent
import io.blockv.common.model.Message
import io.blockv.common.model.Pack
import io.blockv.common.model.StateUpdateEvent
import io.blockv.common.model.Vatom
import io.blockv.common.model.WebSocketEvent
import io.blockv.common.util.JsonUtil
import io.blockv.core.internal.repository.model.VatomIndex
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class InventoryImpl(
  val vatomApi: VatomApi,
  val webSocket: Websocket,
  val jsonModule: JsonModule,
  val database: Database
) : Inventory {

  var state: Message.State = Message.State.UNSTABLE
  val dbLock = Semaphore(1)
  var disposable: Disposable? = null
  val vatoms = HashMap<String, Vatom>()
  var emitter: FlowableEmitter<Message<Item<Vatom>>>? = null
  val inventory =
    Flowable.create<Message<Item<Vatom>>>({ emitter ->
      this.emitter = emitter
      val disposable = CompositeDisposable()
      val messages = ArrayList<WebSocketEvent<JSONObject>>()
      emitter.setDisposable(disposable)
      val processMessage: (message: WebSocketEvent<JSONObject>) -> Unit =
        { message ->
          if (message.type == WebSocketEvent.MessageType.INVENTORY) {
            val event = jsonModule.deserialize<InventoryEvent>(message.payload!!)
            val dbVatoms = database.get<VatomIndex>("vatom", listOf(event.vatomId))
              .blockingGet()
            if (dbVatoms.isEmpty()) {
              try {
                val vatoms =
                  updateDatabase(vatomApi.getVatomJson(VatomRequest(listOf(event.vatomId))).payload).blockingFirst()
                if (vatoms.isNotEmpty()) {
                  emitter.onNext(
                    Message(
                      Item(vatoms.first(), vatoms.first().property.parentId),
                      Message.Type.ADDED,
                      Message.State.STABLE
                    )
                  )
                }
              } catch (e: Exception) {
                e.printStackTrace()
              }
            } else
              if (dbVatoms.first().ownerId != event.newOwnerId) {
                synchronized(vatoms)
                {
                  vatoms.remove(event.vatomId)
                }
                dbLock.acquire()
                try {
                  database.delete("vatom", listOf(event.vatomId)).blockingGet()
                } finally {
                  dbLock.release()
                }
                val vatom = jsonModule.deserialize<Vatom>(dbVatoms.first().data)
                emitter.onNext(
                  Message(
                    Item(vatom, vatom.property.parentId),
                    Message.Type.REMOVED,
                    Message.State.STABLE
                  )
                )
              }
          } else
            if (message.type == WebSocketEvent.MessageType.STATE_UPDATE) {
              val event = jsonModule.deserialize<StateUpdateEvent>(message.payload!!)
              if (event.operation.toLowerCase() == "update") {
                try {
                  val dbVatoms = database.get<VatomIndex>("vatom", listOf(event.vatomId))
                    .blockingGet()
                  if (dbVatoms.isNotEmpty()) {
                    JsonUtil.merge(dbVatoms.first().data, event.vatomProperties)
                    val vatom = jsonModule.deserialize<Vatom>(dbVatoms.first().data)
                    val old = vatoms[vatom.id]
                    vatom.faces = old?.faces ?: emptyList()
                    vatom.actions = old?.actions ?: emptyList()
                    synchronized(vatoms)
                    {
                      vatoms[vatom.id] = vatom
                    }
                    emitter.onNext(
                      Message(
                        Item(
                          vatom, old?.property?.parentId ?: vatom.property.parentId
                        ),
                        Message.Type.UPDATED,
                        Message.State.STABLE
                      )
                    )
                    dbLock.acquire()
                    try {
                      val throwable = database.addOrUpdate("vatom", dbVatoms).blockingGet()
                      throwable?.printStackTrace()
                    } finally {
                      dbLock.release()
                    }
                  }
                } catch (e: Exception) {
                  e.printStackTrace()
                }
              }
            }
        }
      synchronized(this) {
        state = Message.State.UNSTABLE
        val timer = Flowable.timer(300, TimeUnit.MILLISECONDS)
          .observeOn(Schedulers.computation())
          .doFinally {
            synchronized(vatoms)
            {
              vatoms.clear()
              database.deleteAll("vatom").blockingGet()
              database.deleteAll("face").blockingGet()
              database.deleteAll("action").blockingGet()
            }
            emitter.onNext(Message(emptyList(), Message.Type.ADDED, state))
            disposable.add(
              fetchInventory()
                .observeOn(Schedulers.io())
                .doOnComplete {
                  synchronized(this) {
                    emitter.onNext(Message(emptyList(), Message.Type.ADDED, Message.State.STABLE))
                    messages.forEach { processMessage(it) }
                    state = Message.State.STABLE
                  }
                }
                .subscribe({ vatoms ->
                  synchronized(this) {
                    if (state == Message.State.UNSTABLE) {
                      state = Message.State.STABILISING
                      emitter.onNext(
                        Message(
                          vatoms.map { Item(it, it.property.parentId) },
                          Message.Type.INITIAL,
                          Message.State.STABILISING
                        )
                      )
                    } else {
                      emitter.onNext(
                        Message(
                          vatoms.map { Item(it, it.property.parentId) },
                          Message.Type.ADDED,
                          Message.State.STABILISING
                        )
                      )
                    }
                  }

                }, {
                  emitter.onError(it)
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
              if (state != Message.State.STABLE) {
                messages.add(message)
              } else {
                processMessage(message)
              }
            }
          }, {
            emitter.onNext(Message(emptyList(), Message.Type.ADDED, Message.State.UNSTABLE))
            emitter.onError(it)
          })
        )

      }

    }, BackpressureStrategy.BUFFER)
      .subscribeOn(Schedulers.io())
      .doOnError {
        it.printStackTrace()
        synchronized(this)
        {
          state = Message.State.UNSTABLE
        }
      }
      .retryWhen { errors ->
        errors.flatMap { error ->
          if (error is DatapoolException) {
            if (error.error == DatapoolException.Error.REGION_DISPOSED) {
              Flowable.error(error)
            } else
              Flowable.just(error)
                .delay(300, TimeUnit.MILLISECONDS)
          } else
            Flowable.just(error)
              .delay(3, TimeUnit.SECONDS)
        }
      }
      .share()

  override fun getRegion(id: String): Flowable<Message<Vatom>> {
    return Flowable.create<Message<Vatom>>({ emitter ->
      synchronized(this)
      {
        dbLock.acquire()
        try {
          val vatoms = getCachedVatoms(id, false)
            .blockingGet()
            .filter { it.property.parentId == id }

          emitter.onNext(Message(vatoms, Message.Type.INITIAL, state))
          emitter.setDisposable(
            inventory
              .observeOn(Schedulers.computation())
              .subscribe { message ->
                if (message.type == Message.Type.UPDATED) {
                  val items = message.items.filter { it.value.property.parentId == id || it.region == id }
                  val added = ArrayList<Vatom>()
                  val removed = ArrayList<Vatom>()
                  val updated = ArrayList<Vatom>()

                  items.forEach {
                    if (it.value.property.parentId != it.region) {
                      if (it.value.property.parentId == id) {
                        added.add(it.value)
                      } else {
                        removed.add(it.value)
                      }
                    } else {
                      updated.add(it.value)
                    }
                  }

                  if (added.isNotEmpty()) {
                    emitter.onNext(Message(added, Message.Type.ADDED, message.state))
                  }
                  if (removed.isNotEmpty()) {
                    emitter.onNext(Message(removed, Message.Type.REMOVED, message.state))
                  }
                  if (updated.isNotEmpty()) {
                    emitter.onNext(Message(updated, Message.Type.UPDATED, message.state))
                  }

                } else {
                  emitter.onNext(Message(
                    message.items
                      .filter { it.value.property.parentId == id || it.region == id }
                      .map {
                        it.value
                      },
                    message.type,
                    message.state
                  )
                  )
                }
              })

        } finally {
          dbLock.release()
        }
        if (disposable == null || disposable!!.isDisposed) {
          disposable = inventory.subscribe()
        }
      }
    }, BackpressureStrategy.BUFFER)
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.computation())
      .filter {
        it.items.isNotEmpty()
          || it.type == Message.Type.INITIAL
          || (it.type == Message.Type.ADDED)
      }
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun getVatom(id: String): Flowable<Message<Vatom>> {
    return Flowable.create<Message<Vatom>>({ emitter ->
      synchronized(this)
      {
        dbLock.acquire()
        try {
          val vatoms = getCachedVatoms(null, false)
            .blockingGet()
            .filter { it.id == id }

          emitter.onNext(Message(vatoms, Message.Type.INITIAL, state))
          emitter.setDisposable(
            inventory
              .observeOn(Schedulers.computation())
              .map { message ->
                Message(
                  message.items
                    .filter { it.value.id == id }
                    .map { it.value },
                  message.type,
                  message.state
                )
              }
              .subscribe {
                emitter.onNext(it)
              })

        } finally {
          dbLock.release()
        }
        if (disposable == null || disposable!!.isDisposed) {
          disposable = inventory.subscribe()
        }
      }
    }, BackpressureStrategy.BUFFER)
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.computation())
      .filter {
        it.items.isNotEmpty()
          || it.type == Message.Type.INITIAL
          || (it.type == Message.Type.ADDED && it.state == Message.State.STABLE)
      }
      .observeOn(AndroidSchedulers.mainThread())
  }

  private fun getCachedVatoms(parentId: String? = null, needLock: Boolean = true): Single<List<Vatom>> {
    return Single.fromCallable {
      synchronized(vatoms)
      {
        vatoms.values
      }
    }
      .observeOn(Schedulers.computation())
      .flatMap { cached ->
        if (cached.isNotEmpty()) {
          Single.just(ArrayList(cached))
        } else {
          Completable.fromCallable {
            if (needLock) dbLock.acquire()
          }
            .subscribeOn(Schedulers.io())
            .andThen(
              database.execute(
                listOf(
                  Database.Query(
                    "vatom",
                    if (parentId != null) "parent_id = '$parentId'" else null,
                    "when_modified DESC",
                    100
                  ),
                  Database.Query("action"),
                  Database.Query("face")
                )
              )
            )
            .doFinally { if (needLock) dbLock.release() }
            .observeOn(Schedulers.computation())
            .map { data ->
              val actions = (data[1] as List<JSONObject>).map { action ->
                jsonModule.deserialize<Action>(action)
              }
              val faces = (data[2] as List<JSONObject>).map { face ->
                jsonModule.deserialize<Face>(face)
              }
              val vatoms = data[0] as List<VatomIndex>
              val faceMap = HashMap<String, ArrayList<Face>>()
              val actionMap = HashMap<String, ArrayList<Action>>()
              faces.forEach { face ->
                if (!faceMap.containsKey(face.templateId)) {
                  faceMap[face.templateId] = ArrayList()
                }
                if (faceMap[face.templateId]!!.find { it.id == face.id } == null) {
                  faceMap[face.templateId]!!.add(face)
                }
              }
              actions.forEach { action ->
                if (!actionMap.containsKey(action.templateId)) {
                  actionMap[action.templateId] = ArrayList()
                }
                if (actionMap[action.templateId]!!.find { it.name == action.name } == null) {
                  actionMap[action.templateId]!!.add(action)
                }
              }
              val out = vatoms.map {
                val vatom = jsonModule.deserialize<Vatom>(it.data)
                vatom.actions = actionMap[vatom.property.templateId] ?: ArrayList()
                vatom.faces = faceMap[vatom.property.templateId] ?: ArrayList()
                vatom
              }
              synchronized(vatoms)
              {
                this.vatoms.clear()
                out.forEach {
                  this.vatoms[it.id] = it
                }

              }
              out
            }
            .observeOn(AndroidSchedulers.mainThread())
        }

      }.observeOn(AndroidSchedulers.mainThread())
  }

  fun fetchInventory(): Observable<List<Vatom>> {
    return Observable.create<JSONObject> { emitter ->
      var page = 1
      do {
        val result = vatomApi.getInventoryJson(InventoryRequest("*", page, 100))
        page += 1
        emitter.onNext(result.payload)
      } while (result.payload.has("vatoms")
        && result.payload.getJSONArray("vatoms").length() > 0
        && !emitter.isDisposed
      )
      emitter.onComplete()
    }.subscribeOn(Schedulers.io())
      .flatMap { payload ->
        updateDatabase(payload)
      }
  }

  private fun updateDatabase(payload: JSONObject): Observable<List<Vatom>> {
    return Observable.fromCallable {
      dbLock.acquire()
      try {
        val vatoms = payload.getJSONArray("vatoms")
        val faces = payload.getJSONArray("faces")
        val actions = payload.getJSONArray("actions")
        database.addOrUpdate("vatom", (0 until vatoms.length()).map {
          val vatom = vatoms.getJSONObject(it)
          val properties = vatom.getJSONObject("vAtom::vAtomType")
          VatomIndex(
            vatom.getString("id"),
            properties.getString("parent_id"),
            vatom.getString("when_modified"),
            properties.getString("template"),
            properties.getString("template_variation"),
            properties.getString("owner"),
            vatom
          )
        })
          .andThen(
            database.addOrUpdate("face", (0 until faces.length()).map { faces.get(it) })
          )
          .andThen(
            database.addOrUpdate("action", (0 until actions.length()).map { actions.get(it) })
          )
          .blockingGet()
      } finally {
        dbLock.release()
      }
      payload
    }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.computation())
      .map { data ->
        val vatoms = data.getJSONArray("vatoms")
        val faces = data.getJSONArray("faces")
        val actions = data.getJSONArray("actions")
        Pack((0 until vatoms.length()).map {
          jsonModule.deserialize<Vatom>(vatoms.getJSONObject(it))
        }, (0 until faces.length()).map {
          jsonModule.deserialize<Face>(faces.getJSONObject(it))
        }
          , (0 until actions.length()).map {
            jsonModule.deserialize<Action>(actions.getJSONObject(it))
          }
        )
      }
      .map { pack ->
        val faceMap = HashMap<String, ArrayList<Face>>()
        val actionMap = HashMap<String, ArrayList<Action>>()
        pack.faces.forEach { face ->
          if (!faceMap.containsKey(face.templateId)) {
            faceMap[face.templateId] = ArrayList()
          }
          if (faceMap[face.templateId]!!.find { it.id == face.id } == null) {
            faceMap[face.templateId]!!.add(face)
          }
        }
        pack.actions.forEach { action ->
          if (!actionMap.containsKey(action.templateId)) {
            actionMap[action.templateId] = ArrayList()
          }
          if (actionMap[action.templateId]!!.find { it.name == action.name } == null) {
            actionMap[action.templateId]!!.add(action)
          }
        }
        val out = pack.vatoms.map { vatom ->
          vatom.actions = actionMap[vatom.property.templateId] ?: ArrayList()
          vatom.faces = faceMap[vatom.property.templateId] ?: ArrayList()
          vatom
        }

        synchronized(vatoms)
        {
          out.forEach {
            vatoms[it.id] = it
          }
        }
        out
      }
  }

  override fun dispose() {
    disposable?.dispose()
    if (emitter?.isCancelled == false) {
      emitter?.onError(DatapoolException.Error.REGION_DISPOSED.exception())
    }
  }

  override fun reset(): Single<Unit> {
    return Single.fromCallable {
      synchronized(vatoms)
      {
        dispose()
        dbLock.acquire()
        try {
          vatoms.clear()
          database.deleteAll("vatom").blockingGet()
          database.deleteAll("action").blockingGet()
          database.deleteAll("face").blockingGet()
        } finally {
          dbLock.release()
        }
      }
      Unit
    }.subscribeOn(Schedulers.io())
  }

  override fun performAction(action: String, payload: JSONObject): Single<Unit> {
    return Single.fromCallable {
      val action = action.toLowerCase()
      val id = payload.optString("this.id")
      if (id != null) {
        synchronized(vatoms)
        {
          try {
            dbLock.acquire()
            if (state == Message.State.STABLE
              && vatoms.containsKey(id)
              && emitter?.isCancelled == false
            ) {
              val vatom = vatoms[id]!!
              when (action) {
                "transfer", "redeem", "trash" -> {
                  emitter?.onNext(
                    Message(
                      Item(vatom, vatom.property.parentId),
                      Message.Type.REMOVED,
                      state
                    )
                  )
                }
                "drop" -> {
                  vatom.property.isDropped = true
                  emitter?.onNext(
                    Message(
                      Item(vatom, vatom.property.parentId),
                      Message.Type.UPDATED,
                      state
                    )
                  )
                }
              }
            }
          } finally {
            dbLock.release()
          }
        }
      }
    }
      .subscribeOn(Schedulers.io())
  }

  override fun setParentId(vatomId: String, parentId: String): Single<String> {
    return Single.fromCallable<String> {
      var oldParent: String? = ""
      synchronized(vatoms)
      {
        try {
          dbLock.acquire()
          if (state == Message.State.STABLE
            && vatoms.containsKey(vatomId)
            && emitter?.isCancelled == false
          ) {
            val vatom = vatoms[vatomId]!!
            oldParent = vatom.property.parentId
            vatom.property.parentId = parentId
            emitter?.onNext(
              Message(
                Item(vatom, parentId),
                Message.Type.ADDED,
                state
              )
            )
            emitter?.onNext(
              Message(
                Item(vatom, oldParent),
                Message.Type.REMOVED,
                state
              )
            )
          }
        } finally {
          dbLock.release()
        }
      }
      oldParent ?: ""
    }
      .subscribeOn(Schedulers.io())
  }

  override fun invalidate() {
    if (emitter?.isCancelled == false) {
      emitter?.onError(DatapoolException.Error.REGION_INVALIDATED.exception())
    }
  }
}