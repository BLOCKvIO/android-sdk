package io.blockv.core.internal.datapool

import android.content.Context
import androidx.room.Room
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.internal.net.rest.api.VatomApi
import io.blockv.common.internal.net.websocket.Websocket
import io.blockv.common.internal.repository.Preferences
import io.blockv.core.internal.repository.Database

class Datapool(
  context: Context,
  vatomApi: VatomApi,
  websocket: Websocket,
  jsonModule: JsonModule,
  preferences: Preferences
) {

  val db = Room.databaseBuilder(
    context,
    Database::class.java,
    "datapool-db"
  )
    .fallbackToDestructiveMigration()
    .build()

  val map: GeoMap = GeoMapImpl(vatomApi, websocket, jsonModule)
  val inventory: Inventory = InventoryImpl(vatomApi, websocket, jsonModule, db, preferences)
}