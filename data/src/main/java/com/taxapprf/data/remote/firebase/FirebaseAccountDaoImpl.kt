package com.taxapprf.data.remote.firebase

import com.taxapprf.data.remote.firebase.dao.FirebaseAccountDao
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.data.safeCall
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAccountDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : FirebaseAccountDao {
    override suspend fun getAccounts() =
        safeCall {
            fb.getAccountsPath()
                .get()
                .await()
                .children
                .mapNotNull { ds ->
                    ds.getValue(FirebaseAccountModel::class.java)?.toAccountModel(ds.key)
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
            println("@@@@" + firebaseAccountModels)
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