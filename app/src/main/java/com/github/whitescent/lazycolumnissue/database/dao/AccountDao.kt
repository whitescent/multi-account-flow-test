package com.github.whitescent.lazycolumnissue.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.whitescent.lazycolumnissue.database.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

  @Upsert
  suspend fun insertOrUpdate(account: AccountEntity)

  @Insert(entity = AccountEntity::class)
  suspend fun insert(account: AccountEntity)

  @Query("SELECT * FROM ACCOUNTENTITY WHERE id = :id")
  suspend fun getAccount(id: Long): AccountEntity?

  @Query("SELECT * FROM ACCOUNTENTITY")
  fun getAccountListFlow(): Flow<List<AccountEntity>>

  @Query("SELECT * FROM ACCOUNTENTITY WHERE isActive = 1 LIMIT 1")
  fun getActiveAccountFlow(): Flow<AccountEntity?>

  @Query("SELECT * FROM ACCOUNTENTITY WHERE isActive = 1 LIMIT 1")
  suspend fun getActiveAccount(): AccountEntity?

  @Query("SELECT * FROM ACCOUNTENTITY WHERE id = :id AND domain = :domain")
  suspend fun getAccountByInstanceInfo(id: Long, domain: String): AccountEntity?

  @Delete
  suspend fun delete(account: AccountEntity)

  @Query("UPDATE AccountEntity SET isActive = :isActive WHERE id = :accountId")
  suspend fun setAccountActiveState(accountId: Long, isActive: Boolean)

  @Transaction
  suspend fun setActiveAccount(accountId: Long) {
    deactivateCurrentlyActiveAccount()
    setAccountActiveState(accountId, true)
  }

  @Transaction
  suspend fun addAccount(account: AccountEntity) {
    deactivateCurrentlyActiveAccount()
    val existingAccount =
      getAccountByInstanceInfo(account.id, account.domain)

    if (existingAccount != null) {
      insertOrUpdate(account.copy(id = existingAccount.id, isActive = true))
    } else {
      insertOrUpdate(account.copy(isActive = true))
    }
  }

  private suspend fun deactivateCurrentlyActiveAccount() {
    getActiveAccount()?.id?.let { currentAccountId ->
      setAccountActiveState(currentAccountId, false)
    }
  }
}
