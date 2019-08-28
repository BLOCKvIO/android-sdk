package io.blockv.core.internal.repository.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import io.blockv.common.model.Action
import io.blockv.common.model.Face
import io.blockv.common.model.InventorySync
import io.blockv.common.model.Vatom
import io.blockv.core.internal.repository.model.VatomPack
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface VatomDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun addOrUpdateVatoms(vatoms: List<Vatom>): Single<List<Long>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun addOrUpdateFaces(vatoms: List<Face>): Single<List<Long>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun addOrUpdateActions(vatoms: List<Action>): Single<List<Long>>

  @Delete
  fun removeVatom(vatoms: List<Vatom>): Single<Int>

  @Query("DELETE FROM vatom WHERE id IN (:id)")
  fun removeVatomById(id: List<String>): Single<Int>

  @Query("DELETE FROM vatom")
  fun removeAll(): Single<Int>

  @Query("SELECT *, COUNT(id) as count FROM vatom WHERE id IN (:ids)")
  fun getVatoms(ids: List<String>): Flowable<List<VatomPack>>

  @RawQuery(observedEntities = [Vatom::class])
  fun getVatoms(query: SupportSQLiteQuery): DataSource.Factory<Int, VatomPack>

  @Query("SELECT DISTINCT LOWER(category) FROM vatom ORDER BY LOWER(category)")
  fun getCategories(): Single<List<String>>

  @Query("SELECT id, sync FROM vatom")
  fun getVatomSync(): Single<List<InventorySync.VatomSync>>
}