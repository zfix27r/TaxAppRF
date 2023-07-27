package com.taxapprf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.entity.AccountEntity
import com.taxapprf.data.local.model.FirebaseAccountModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM account WHERE active = 1 LIMIT 1")
    fun getUserModel(): Flow<AccountEntity?>

    @Query("SELECT id FROM account")
    fun getAccounts(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = AccountEntity::class)
    fun save(accounts: List<FirebaseAccountModel>): List<Long>

    @Query("UPDATE account SET active = 1 WHERE id = :id")
    fun save(id: String): Int
}