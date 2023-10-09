package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalAccountDao {
    @Query("SELECT * FROM account WHERE user_id = :userId")
    fun observeUserAccounts(userId: Int): Flow<List<LocalAccountEntity>>

    @Query("UPDATE account SET is_active = 0 WHERE is_active = 1")
    fun resetActiveAccount(): Int

    @Query("UPDATE account SET is_active = 1 WHERE remote_key = :accountName")
    fun setActiveAccount(accountName: String): Int

    @Query("DELETE FROM account")
    fun deleteAll()
}