package com.github.whitescent.lazycolumnissue.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val username: String,
  val domain: String,
  val isActive: Boolean,
  val index: Int = 0,
  val offset: Int = 0
)
