package com.taxapprf.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun getAccounts(): Flow<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAccount(accountEntity: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAccounts(accountEntities: List<AccountEntity>)

    @Query("UPDATE account SET active = 0")
    fun resetActiveAccount(): Int

    @Query("DELETE FROM account")
    fun drop()
}