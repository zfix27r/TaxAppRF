package com.taxapprf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.entity.TaxesEntity
import com.taxapprf.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM taxes WHERE account = :account")
    fun getTaxes(account: String): Flow<List<TaxesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userEntity: UserEntity): Long
}