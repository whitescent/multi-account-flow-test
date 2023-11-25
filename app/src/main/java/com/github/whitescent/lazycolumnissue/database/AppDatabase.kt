package com.github.whitescent.lazycolumnissue.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.whitescent.lazycolumnissue.database.dao.AccountDao
import com.github.whitescent.lazycolumnissue.database.dao.TimelineDao

@Database(
  entities = [
    TimelineEntity::class,
    AccountEntity::class
  ],
  version = 1
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun timelineDao(): TimelineDao
  abstract fun accountDao(): AccountDao
}
