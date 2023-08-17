package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.taxapprf.data.remote.firebase.dao.FirebaseReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.safeCall
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirebaseReportDaoImpl(
    private val fb: FirebaseAPI,
) : FirebaseReportDao {
    override fun getReports(accountKey: String) =
        callbackFlow<Result<List<ReportModel>>> {
            safeCall {
                val reference = fb.getReportsPath(accountKey)

                val callback = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val reports = snapshot.children.mapNotNull {
                            it.getValue(FirebaseReportModel::class.java)?.toReportModel()
                        }
                        trySendBlocking(Result.success(reports))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySendBlocking(Result.failure(error.toException()))
                    }
                }

                reference.addValueEventListener(callback)

                awaitClose { reference.removeEventListener(callback) }
            }
        }

    override suspend fun addReport(saveReportModel: SaveReportModel) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReport(saveReportModel: SaveReportModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReport(deleteReportModel: DeleteReportModel) {
        safeCall {
            with(deleteReportModel) {
                fb.getReportPath(accountKey, year)
                    .setValue(null)
                    .await()

                fb.getTransactionsPath(accountKey, year)
                    .setValue(null)
                    .await()
            }
        }
    }

    override suspend fun getReportTax(accountKey: String, year: String) =
        fb.getReportPath(accountKey, year)
            .get()
            .await()
            .getValue(FirebaseReportModel::class.java)

    override suspend fun saveReportTax(
        accountKey: String, year: String, firebaseReportModel: FirebaseReportModel
    ) {
        fb.getReportPath(accountKey, year)
            .setValue(firebaseReportModel)
            .await()
    }
}