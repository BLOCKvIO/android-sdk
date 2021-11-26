package io.blockv.core.internal.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.blockv.common.internal.repository.converter.ChildPolicyListConverter
import io.blockv.common.internal.repository.converter.JsonConverter
import io.blockv.common.internal.repository.converter.NumberListConverter
import io.blockv.common.internal.repository.converter.ResourceListConverter
import io.blockv.common.internal.repository.converter.StringListConverter
import io.blockv.common.model.Action
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.core.internal.repository.dao.VatomDao

@Database(entities = [Vatom::class, Face::class, Action::class], version = 6)
@TypeConverters(
  JsonConverter::class,
  StringListConverter::class,
  ResourceListConverter::class,
  NumberListConverter::class,
  ChildPolicyListConverter::class
)
abstract class Database : RoomDatabase() {
  abstract fun vatomDao(): VatomDao
}