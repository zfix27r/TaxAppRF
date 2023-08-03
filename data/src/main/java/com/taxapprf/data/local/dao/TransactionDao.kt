package com.taxapprf.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.taxapprf.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction` WHERE account = :account AND year = :year")
    fun getTransactions(account: String, year: String): Flow<List<TransactionEntity>>
}