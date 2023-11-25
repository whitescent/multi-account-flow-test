package com.github.whitescent.lazycolumnissue.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.whitescent.lazycolumnissue.database.TimelineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimelineDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(vararg timelineEntity: TimelineEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(timelineEntity: List<TimelineEntity>)

  @Query(
    """
      SELECT * FROM timelineentity WHERE accountId = :accountId AND id = :statusId LIMIT 1
    """
  )
  suspend fun getSingleStatusWithId(accountId: Long, statusId: String): TimelineEntity?

  @Query("SELECT * FROM timelineentity WHERE accountId = :accountId")
  fun getStatusListWithFlow(accountId: Long): Flow<List<TimelineEntity>>

  @Query("SELECT * FROM timelineentity WHERE accountId = :accountId")
  suspend fun getStatusListWith(accountId: Long): List<TimelineEntity>

  @Query("DELETE FROM timelineentity WHERE accountId = :accountId")
  suspend fun clearAll(accountId: Long)
}
