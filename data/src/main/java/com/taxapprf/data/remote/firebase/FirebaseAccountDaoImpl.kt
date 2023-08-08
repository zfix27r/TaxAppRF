package com.taxapprf.data.remote.firebase

import com.taxapprf.data.error.AuthError
import com.taxapprf.data.remote.firebase.dao.FirebaseAccountDao
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.data.safeCall
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAccountDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : FirebaseAccountDao {
    override suspend fun getAccounts(): List<FirebaseAccountModel> =
        safeCall {
            println("!!!!!!!")
            println(fb.getAccountsPath())
            println("!!!!!!!")
            fb.getAccountsPath()
                .get()
                .await()
                .children
                .mapNotNull { it.getValue(FirebaseAccountModel::class.java) }
        }


    override suspend fun saveAccount(firebaseAccountModel: FirebaseAccountModel) {
        safeCall {
            try {
                println("@@ " + firebaseAccountModel)
                val r = fb.getAccountsPath()
                    .push()
                    .setValue(firebaseAccountModel)
                    .await()
                println("@@ " + r)
            } catch (e: Exception) {
                println(e.message)
                println(e)
            }
        }
    }

    override suspend fun saveDefaultAccount() {
        val firebaseAccountModel = FirebaseAccountModel("default", true)
        saveAccount(firebaseAccountModel)
    }
}