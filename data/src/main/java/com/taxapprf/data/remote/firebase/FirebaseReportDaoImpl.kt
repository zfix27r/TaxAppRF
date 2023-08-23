package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.taxapprf.data.error.external.DataErrorExternalGetReport
import com.taxapprf.data.remote.firebase.dao.FirebaseReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.safeCall
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.GetReportModel
import com.taxapprf.domain.report.GetReportsModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirebaseReportDaoImpl(
    private val fb: FirebaseAPI,
) : FirebaseReportDao {
    override fun getReports(getReportsModel: GetReportsModel) =
        callbackFlow<Result<List<ReportModel>>> {
            safeCall {
                val reference = fb.getReportsPath(getReportsModel.accountKey)

                val callback = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val reports = snapshot.children.mapNotNull {
                            it.getValue(FirebaseReportModel::class.java)?.toReportModel()
                        }
                        trySendBlocking(Result.success(reports))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySendBlocking(Result.failure(DataErrorExternalGetReport(error.message)))
                    }
                }

                reference.addValueEventListener(callback)

                awaitClose { reference.removeEventListener(callback) }
            }
        }

    override suspend fun getReport(getReportModel: GetReportModel) =
        safeCall {
            with(getReportModel) {
                fb.getReportPath(accountKey, yearKey)
                    .get()
                    .await()
                    .getValue(FirebaseReportModel::class.java)
                    ?.toReportModel()
                    ?: getDefaultReportModel(yearKey)
            }
        }

    override suspend fun deleteReport(deleteReportModel: DeleteReportModel) {
        safeCall {
            with(deleteReportModel) {
                fb.getReportPath(accountKey, yearKey)
                    .setValue(null)
                    .await()

                fb.getTransactionsPath(accountKey, yearKey)
                    .setValue(null)
                    .await()
            }
        }
    }

    override suspend fun saveReport(saveReportModel: SaveReportModel) {
        safeCall {
            with(saveReportModel) {
                fb.getReportPath(accountKey, yearKey)
                    .setValue(saveReportModel.toFirebaseReportModel())
                    .await()
            }
        }
    }

    private fun getDefaultReportModel(getYear: String) = ReportModel(
        year = getYear,
        tax = 0.0,
        size = 0,
    )

    private fun SaveReportModel.toFirebaseReportModel() =
        FirebaseReportModel(yearKey, tax, size)
}