package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalReportsDao {
    @Query("SELECT * FROM report WHERE account_id = :accountId ORDER BY remote_key DESC")
    fun observeAccountReports(accountId: Int): Flow<List<LocalReportEntity>>
}