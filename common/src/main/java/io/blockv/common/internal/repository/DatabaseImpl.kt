package io.blockv.common.internal.repository

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DatabaseImpl(context: Context, val name: String = "blockv.db", val version: Int = 1) : Database {

  private val dbHelper: SQLiteOpenHelper

  @get:Synchronized
  @set:Synchronized
  private var isSetup: Boolean = false

  private val mappers = HashMap<String, Mapper<*>>()

  init {
    dbHelper = object : SQLiteOpenHelper(context, name, null, version) {
      override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_INDEX_TABLE)
      }

      override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        val tables = ArrayList<String>()
        while (c.moveToNext()) {
          tables.add(c.getString(0))
        }
        c.close()
        for (table in tables) {
          val dropQuery = "DROP TABLE IF EXISTS $table"
          db.execSQL(dropQuery)
        }
        onCreate(db)
      }

      override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
      }
    }
  }

  @Synchronized
  fun setup() {
    if (isSetup) return

    val db = dbHelper.writableDatabase

    synchronized(dbHelper) {
      val currentTables = HashMap<String, Int>()
      val cursor = db.rawQuery("SELECT * FROM table_index", null)
      cursor.moveToFirst()
      while (!cursor.isAfterLast) {
        val id = cursor.getString(cursor.getColumnIndex("id"))
        val version = cursor.getInt(cursor.getColumnIndex("version"))
        currentTables[id] = version
        cursor.moveToNext()
      }
      cursor.close()
      db.beginTransaction()
      try {
        mappers.values
          .forEach { mapper ->
            val table = mapper.table
            val tableName = table.name
            val version = table.hashCode()
            if (!currentTables.containsKey(tableName) || currentTables[tableName] != version) {
              db.execSQL("DROP TABLE IF EXISTS $tableName")
              var command = "CREATE TABLE $tableName ( _id TEXT PRIMARY KEY ON CONFLICT REPLACE"
              table.columns.forEach {
                command += ",${it.name} ${it.type.value}"
              }
              command += ");"
              db.execSQL(command)
              val update = "INSERT INTO table_index VALUES('$tableName',$version)"
              db.execSQL(update)
            }

          }
        db.setTransactionSuccessful()
      } finally {
        db.endTransaction()
      }
    }
    isSetup = true
  }

  override fun init(): Completable {
    return Completable.fromCallable {
      setup()
    }.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun addMapper(mapper: Mapper<*>): Database {
    if (isSetup) throw Exception("Database is already setup")
    mappers[mapper.table.name] = mapper
    return this
  }

  override fun <T> addOrUpdate(table: String, items: List<T>): Completable {
    return Completable.fromSingle(
      init()
        .observeOn(Schedulers.computation())
        .andThen(Single.fromCallable {
          val mapper = mappers[table] ?: throw Exception("No mapper for table $table")
          mapper as Mapper<T>
          items.map { mapper.db(it) }
        })
        .observeOn(Schedulers.io())
        .map { rows ->
          val db = dbHelper.writableDatabase
          synchronized(dbHelper) {
            try {
              db.beginTransaction()
              rows.forEach {
                var command = "INSERT INTO $table (_id"
                it.values.forEach {
                  command += ",${it.name}"
                }
                command += ") VALUES ('${it.id}'"
                it.values.forEach {
                  command += ",${it.value}"
                }
                command += ")"
                db.execSQL(command)
              }
              db.setTransactionSuccessful()
            } finally {
              db.endTransaction()
            }
          }
        }
        .doOnError { it.printStackTrace() }
    )

  }

  override fun delete(table: String, ids: List<String>): Completable {
    return init()
      .observeOn(Schedulers.io())
      .andThen(
        Completable.fromCallable {
          val db = dbHelper.writableDatabase
          synchronized(dbHelper) {
            try {
              db.beginTransaction()
              ids.forEach {
                db.execSQL("DELETE FROM $table WHERE _id = '$it'")
              }
              db.setTransactionSuccessful()
            } finally {
              db.endTransaction()
            }
          }
        })
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun deleteAll(table: String): Completable {
    return init()
      .observeOn(Schedulers.io())
      .andThen(
        Completable.fromCallable {
          val db = dbHelper.writableDatabase
          synchronized(dbHelper) {
            db.execSQL("DELETE FROM $table ")
          }
        })
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun <T> getAll(table: String, where: String?): Single<List<T>> {
    return init()
      .observeOn(Schedulers.io())
      .andThen(Single.fromCallable {
        val mapper = mappers[table] ?: throw Exception("No mapper for table $table")
        val db = dbHelper.readableDatabase
        val items = ArrayList<Map<String, Any>>()
        synchronized(dbHelper) {
          var cursor: Cursor? = null
          try {
            val tableData = mapper.table
            db.beginTransaction()
            cursor = db.rawQuery("SELECT * FROM $table ${if (where != null) "WHERE $where" else ""}", null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
              val data = mapCursor(tableData, cursor)
              if (data != null) {
                items.add(data)
              }
              cursor.moveToNext()
            }
            db.setTransactionSuccessful()
          } finally {
            cursor?.close()
            db.endTransaction()
          }
        }
        items
      })
      .observeOn(Schedulers.computation())
      .map {
        val mapper = mappers[table]!!
        mapper as Mapper<T>
        val list = ArrayList<T>()
        it.forEach {
          list.add(mapper.model(it))
        }
        list as List<T>
      }
      .observeOn(AndroidSchedulers.mainThread())
  }

  override fun <T> get(table: String, ids: List<String>): Single<List<T>> {
    return init()
      .observeOn(Schedulers.io())
      .andThen(Single.fromCallable {
        val mapper = mappers[table] ?: throw Exception("No mapper for table $table")
        val db = dbHelper.readableDatabase
        val items = ArrayList<Map<String, Any>>()
        synchronized(dbHelper) {
          var cursor: Cursor? = null
          try {
            val tableData = mapper.table
            db.beginTransaction()
            ids.forEach {
              cursor = db.rawQuery("SELECT * FROM $table WHERE _id = '$it'", null)
              cursor!!.moveToFirst()
              val data = mapCursor(tableData, cursor!!)
              if (data != null) {
                items.add(data)
              }
              cursor!!.close()
              cursor = null
            }
            db.setTransactionSuccessful()
          } finally {
            cursor?.close()
            db.endTransaction()
          }
        }
        items
      })
      .observeOn(Schedulers.computation())
      .map {
        val mapper = mappers[table]!!
        mapper as Mapper<T>
        val list = ArrayList<T>()
        it.forEach {
          list.add(mapper.model(it))
        }
        list as List<T>
      }
      .observeOn(AndroidSchedulers.mainThread())

  }

  override fun execute(queries: List<Database.Query>): Single<List<List<Any>>> {
    return init()
      .observeOn(Schedulers.io())
      .andThen(Single.fromCallable {
        val db = dbHelper.readableDatabase
        val out = ArrayList<ArrayList<Map<String, Any>>>()
        var cursor: Cursor? = null
        synchronized(dbHelper) {
          try {
            db.beginTransaction()
            queries.forEach { query ->
              val items = ArrayList<Map<String, Any>>()
              out.add(items)
              val mapper = mappers[query.table] ?: throw Exception("No mapper for table ${query.table}")
              cursor = db.rawQuery(
                "SELECT * FROM ${query.table} " +
                  if (query.where != null) "WHERE ${query.where} " else ""
                    + if (query.orderBy != null) "ORDER BY ${query.orderBy} " else ""
                    + if (query.limit != null) "LIMIT ${query.limit} " else ""
                ,
                null
              )
              cursor!!.moveToFirst()
              while (!cursor!!.isAfterLast) {
                val data = mapCursor(mapper.table, cursor!!)
                if (data != null) {
                  items.add(data)
                }
                cursor!!.moveToNext()
              }
              cursor!!.close()
              cursor = null
            }
            db.setTransactionSuccessful()
          } finally {
            cursor?.close()
            db.endTransaction()
          }
          out
        }
      })
      .observeOn(Schedulers.computation())
      .map {
        it.mapIndexed { index, value ->
          val mapper = mappers[queries.get(index).table]!!
          value.map {
            mapper.model(it)!!
          }
        }
      }
      .observeOn(AndroidSchedulers.mainThread())
  }

  private fun mapCursor(table: Mapper.Table, cursor: Cursor): Map<String, Any>? {
    val data = HashMap<String, Any>()
    val names = cursor.columnNames.toList()
    if (cursor.count > 0) {
      data["_id"] = cursor.getString(0)
      (1 until names.size)
        .forEach { index ->
          val name = names[index]
          val column = table.columns[index - 1]
          data[name] = when (column.type) {
            Mapper.Table.Type.BOOLEAN -> cursor.getInt(index) == 1
            Mapper.Table.Type.STRING -> cursor.getString(index)
            Mapper.Table.Type.INTEGER -> cursor.getInt(index)
            Mapper.Table.Type.FLOAT -> cursor.getFloat(index)
            Mapper.Table.Type.DOUBLE -> cursor.getDouble(index)
            Mapper.Table.Type.LONG -> cursor.getLong(index)
          }

        }
      return data
    }
    return null
  }

  companion object {
    private const val CREATE_INDEX_TABLE =
      "CREATE TABLE table_index (" +
        "id TEXT PRIMARY KEY ON CONFLICT REPLACE, " +
        "version INTEGER)"
  }
}