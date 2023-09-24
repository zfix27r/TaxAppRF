package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseReportModel

interface RemoteReportDao {
    suspend fun getKey(
        accountKey: String,
    ): String?

    suspend fun getAll(
        accountKey: String,
    ): List<FirebaseReportModel>

    suspend fun updateAll(
        accountKey: String,
        reportModels: Map<String, FirebaseReportModel?>
    )
}