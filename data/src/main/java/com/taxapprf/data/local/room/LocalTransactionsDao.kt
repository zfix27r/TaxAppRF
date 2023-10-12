package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTransactionsDao {
    @Query("SELECT * FROM report WHERE id = :reportId LIMIT 1")
    fun observeLocalReportEntity(reportId: Int): Flow<LocalReportEntity?>
}