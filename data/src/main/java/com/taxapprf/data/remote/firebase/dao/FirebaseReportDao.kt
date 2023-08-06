package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseReportModel


interface FirebaseReportDao {
    suspend fun getReports(accountKey: String): List<FirebaseReportModel>

}