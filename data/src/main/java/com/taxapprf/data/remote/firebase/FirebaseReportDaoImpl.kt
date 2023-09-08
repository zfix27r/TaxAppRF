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

                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println(fb.getReportsPath(accountKey).get().await())

                val callback = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue(FirebaseReportModel::class.java)
                            ?.toReportModel(snapshot.key)
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

                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println(fb.getReportsPath(accountKey).get().await())

                val callback = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val reports = snapshot.children.mapNotNull {
                            it.getValue(FirebaseReportModel::class.java)?.toReportModel(it.key)
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
                val snapshot = fb.getReportPath(accountKey, yearKey)
                    .get()
                    .await()

                snapshot
                    .getValue(FirebaseReportModel::class.java)
                    ?.toReportModel(snapshot.key)
                    ?: getDefaultReportModel(yearKey)
            }
        }

    override suspend fun delete(accountKey: String, reportKey: String) {
        safeCall {
            fb.getReportPath(accountKey, reportKey)
                .setValue(null)
                .await()
        }
    }

    override suspend fun deleteAll(
        accountKey: String,
        mapFirebaseReportModels: Map<String, FirebaseReportModel?>
    ) {
        safeCall {
            fb.getReportsPath(accountKey)
                .updateChildren(mapFirebaseReportModels)
                .await()
        }
    }

    override suspend fun save(saveReportModel: SaveReportModel) {
        safeCall {
            with(saveReportModel) {
                fb.getReportPath(accountKey, reportKey)
                    .setValue(saveReportModel.toFirebaseReportModel())
                    .await()
            }
        }
    }

    override suspend fun saveAll(
        accountKey: String,
        mapFirebaseReportModels: Map<String, FirebaseReportModel>
    ) {
        safeCall {
            fb.getReportsPath(accountKey)
                .updateChildren(mapFirebaseReportModels)
                .await()
        }
    }

    private fun getDefaultReportModel(getYear: String) =
        ReportModel(
            id = 0,
            key = getYear,
            tax = 0.0,
            size = 0,
            isSync = true,
            isDelete = false,
            syncAt = 0
        )

    private fun SaveReportModel.toFirebaseReportModel() =
        FirebaseReportModel(tax, size, getTime())
}