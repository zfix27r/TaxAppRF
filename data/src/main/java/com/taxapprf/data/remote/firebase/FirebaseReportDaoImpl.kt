package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.taxapprf.data.error.DataErrorExternal
import com.taxapprf.data.error.external.DataErrorExternalGetReport
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.safeCall
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirebaseReportDaoImpl(
    private val fb: FirebaseAPI,
) : RemoteReportDao {
    override fun observe(accountKey: String, reportKey: String) =
        callbackFlow<Result<FirebaseReportModel?>> {
            safeCall {
                val reference =
                    fb.getReportPath(accountKey, reportKey)

                val callback = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue(FirebaseReportModel::class.java)
                            ?.apply {
                                key = snapshot.key
                            }
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
        callbackFlow<Result<List<FirebaseReportModel>>> {
            safeCall {
                val reference = fb.getReportsPath(accountKey)

                val callback = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val reports = snapshot.children.mapNotNull {
                            it.getValue(FirebaseReportModel::class.java)
                                ?.apply {
                                    key = it.key
                                }
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

    /*
        override suspend fun get(getReportModel: GetReportModel) =
            safeCall {
                with(getReportModel) {
                    fb.getReportPath(accountKey, yearKey)
                        .get()
                        .await()
                        .getValue(FirebaseReportModel::class.java)
                        ?.toReportModel(snapshot.key)
                        ?: getDefaultReportModel(yearKey)
                }
            }
    */

    override suspend fun delete(accountKey: String, reportKey: String) {
        safeCall {
            fb.getReportPath(accountKey, reportKey)
                .setValue(null)
                .await()
        }
    }

    override suspend fun deleteAll(accountKey: String, reportModels: List<FirebaseReportModel>) {
        safeCall {
            reportModels.map {
                val path = fb.getReportsPath(accountKey)

                val key = (if (it.key == "") path.push().key else it.key)
                    ?: throw DataErrorExternal()

                path
                    .child(key)
                    .setValue(null)
                    .await()
            }
        }
    }

    /*
        override suspend fun save(saveReportModel: SaveReportModel) {
            safeCall {
                with(saveReportModel) {
                    fb.getReportPath(accountKey, reportKey)
                        .setValue(saveReportModel.toFirebaseReportModel())
                        .await()
                }
            }
        }
    */

    override suspend fun saveAll(accountKey: String, reportModels: List<FirebaseReportModel>) {
        safeCall {
            reportModels.map {
                val path = fb.getReportsPath(accountKey)

                val key = (if (it.key == "") path.push().key else it.key)
                    ?: throw DataErrorExternal()

                path
                    .child(key)
                    .setValue(it)
                    .await()
            }
        }
    }

}