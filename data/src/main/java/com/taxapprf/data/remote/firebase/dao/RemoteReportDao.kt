package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.entity.FirebaseReportEntity

interface RemoteReportDao {
    suspend fun getAll(
        accountKey: String,
    ): List<FirebaseReportEntity>

    suspend fun updateAll(
        accountKey: String,
        reportModels: Map<String, FirebaseReportEntity?>
    )
}