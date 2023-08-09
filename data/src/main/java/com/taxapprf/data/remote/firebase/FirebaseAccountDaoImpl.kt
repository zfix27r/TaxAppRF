package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.taxapprf.data.remote.firebase.dao.FirebaseAccountDao
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.data.safeCall
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAccountDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : FirebaseAccountDao {
    override fun getAccounts() = callbackFlow {
        safeCall {
            val callback = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val accounts =
                        snapshot.children.mapNotNull {
                            it.getValue(FirebaseAccountModel::class.java)
                                ?.toAccountModel(it.key)
                        }

                    trySendBlocking(accounts)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }

            fb.getAccountsPath().addValueEventListener(callback)

            awaitClose {
                fb.auth.currentUser?.uid?.let {
                    fb.getAccountsPath().removeEventListener(callback)
                }
            }
        }
    }

    override suspend fun saveAccount(firebaseAccountModel: FirebaseAccountModel) {
        safeCall {
            fb.getAccountsPath()
                .child(firebaseAccountModel.name!!)
                .setValue(firebaseAccountModel)
                .await()
        }
    }

    override suspend fun saveAccounts(firebaseAccountModels: Map<String, FirebaseAccountModel>) {
        safeCall {
            fb.getAccountsPath()
                .updateChildren(firebaseAccountModels)
                .await()

            fb.getAccountsPath()
                .get().await().children.mapNotNull { println(it) }
        }
    }

    override suspend fun saveDefaultAccount() {
        val firebaseAccountModel = FirebaseAccountModel("default", true)
        saveAccount(firebaseAccountModel)
    }
}