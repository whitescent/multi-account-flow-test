package com.github.whitescent.lazycolumnissue.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  foreignKeys = [
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["accountId"]
    )
  ],
  indices = [Index("accountId")]
)
data class TimelineEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val content: String,
  val accountId: Long
)
