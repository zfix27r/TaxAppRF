package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DatabaseReference
import com.taxapprf.data.remote.firebase.dao.FirebaseReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import kotlinx.coroutines.tasks.await


class FirebaseReportDaoImpl(
    private val reference: DatabaseReference
) : FirebaseReportDao {
    override suspend fun getReports(accountKey: String): List<FirebaseReportModel> =
        reference
            .child(REPORTS)
            .child(accountKey)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseReportModel::class.java) }


    companion object {
        const val REPORTS = "reports"
    }
}