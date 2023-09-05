package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.taxapprf.data.error.external.DataErrorExternalGetReport
import com.taxapprf.data.getTime
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.remote.firebase.model.GetReportModel
import com.taxapprf.data.safeCall
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirebaseReportDaoImpl(
    private val fb: FirebaseAPI,
) : RemoteReportDao {
    override fun observe(accountKey: String, reportKey: String) =
        callbackFlow<Result<List<ReportModel>>> {
            safeCall {
                val reference =
                    fb.getReportPath(accountKey, reportKey)

                val callback = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue(FirebaseReportModel::class.java)
                            ?.toReportModel()
                            ?.let { trySendBlocking(Result.success(listOf(it))) }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySendBlocking(Result.failure(DataErrorExternalGetReport(error.message)))
                    }
                }

                reference.addValueEventListener(callback)

                awaitClose { reference.removeEventListener(callback) }
            }
        }

    override fun observeAll(accountKey: String) =
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
                        trySendBlocking(Result.failure(DataErrorExternalGetReport(error.message)))
                    }
                }

                reference.addValueEventListener(callback)

                awaitClose { reference.removeEventListener(callback) }
            }
        }

    override suspend fun get(getReportModel: GetReportModel) =
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

    override suspend fun delete(accountKey: String, reportKey: String) {
        safeCall {
            fb.getReportPath(accountKey, reportKey)
                .setValue(null)
                .await()

            fb.getTransactionsPath(accountKey, reportKey)
                .setValue(null)
                .await()
        }
    }

    override suspend fun save(saveReportModel: SaveReportModel) {
        safeCall {
            with(saveReportModel) {
                fb.getReportPath(accountKey, key)
                    .setValue(saveReportModel.toFirebaseReportModel(getTime()))
                    .await()
            }
        }
    }

    override suspend fun save(
        accountKey: String,
        reportModels: Map<String, FirebaseReportModel>
    ) {
        safeCall {
            fb.getReportsPath(accountKey)
                .updateChildren(reportModels)
                .await()
        }
    }

    private fun getDefaultReportModel(getYear: String) =
        ReportModel(
            key = getYear,
            tax = 0.0,
            size = 0,
            isSync = true,
            syncAt = 0
        )

    private fun SaveReportModel.toFirebaseReportModel(syncAt: Long) =
        FirebaseReportModel(key, tax, size, syncAt)
}