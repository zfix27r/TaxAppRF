package com.taxapprf.data.remote.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.data.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAccountDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : RemoteAccountDao {

    fun observer(onChildAdded: (FirebaseAccountModel) -> Unit) {
        println("###############")
        callbackFlow<Result<List<FirebaseAccountModel>>> {
            val callback = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    println("onChildAdded $snapshot")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    println("onChildChanged $snapshot")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    println("onChildRemoved $snapshot")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    println("onChildMoved $snapshot")
                }

                override fun onCancelled(error: DatabaseError) {
                    println("onCancelled $error")
                }
            }

            fb.getAccountsPath().addChildEventListener(callback)

            awaitClose { fb.getAccountsPath().removeEventListener(callback) }
        }
    }

    override fun observeAll() = callbackFlow<Result<List<FirebaseAccountModel>>> {
        safeCall {
            val callback = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val accounts =
                        snapshot.children.mapNotNull {
                            it.getValue(FirebaseAccountModel::class.java)
                                ?.apply {
                                    key = it.key
                                }
                        }

                    if (accounts.isEmpty()) {
                        launch(Dispatchers.IO) { saveDefaultAccount() }
                    }

                    trySendBlocking(Result.success(accounts))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySendBlocking(Result.failure(error.toException()))
                }
            }

            if (fb.isUserAuth)
                fb.getAccountsPath().addValueEventListener(callback)

            awaitClose {
                if (!fb.isUserAuth)
                    fb.getAccountsPath().removeEventListener(callback)
            }
        }
    }

    override suspend fun save(firebaseAccountModel: FirebaseAccountModel) {
        safeCall {
            fb.getAccountsPath()
                .child(firebaseAccountModel.key!!)
                .setValue(firebaseAccountModel)
                .await()
        }
    }

    override suspend fun saveAll(accountModels: List<FirebaseAccountModel>) {
        safeCall {
            val path = fb.getAccountsPath()

            accountModels.map { remote ->
                remote.key?.let {
                    path
                        .child(it)
                        .setValue(remote)
                        .await()
                }
            }
        }
    }

    override suspend fun deleteAll(accountModels: List<FirebaseAccountModel>) {
        safeCall {
            val path = fb.getAccountsPath()

            accountModels.map { remote ->
                remote.key?.let {
                    path
                        .child(it)
                        .setValue(remote)
                        .await()
                }
            }
        }
    }

    override suspend fun saveDefaultAccount() {
        val firebaseAccountModel = FirebaseAccountModel(key = "default", isActive = true)
        save(firebaseAccountModel)
    }
}