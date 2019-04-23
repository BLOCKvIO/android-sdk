package io.blockv.common.internal.repository

import io.reactivex.Completable
import io.reactivex.Single

interface Database {

  fun init(): Completable

  fun <T> addOrUpdate(table: String, items: List<T>): Completable

  fun delete(table: String, ids: List<String>): Completable

  fun deleteAll(table: String): Completable

  fun <T> getAll(table: String, where: String? = null): Single<List<T>>

  fun <T> get(table: String, ids: List<String>): Single<List<T>>

  fun execute(queries: List<Query>): Single<List<List<Any>>>

  class Query(
    val table: String,
    val where: String? = null,
    val orderBy: String? = null,
    val limit: Int? = null
  )
}