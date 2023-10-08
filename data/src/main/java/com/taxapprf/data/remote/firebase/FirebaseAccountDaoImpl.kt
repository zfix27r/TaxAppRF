package com.taxapprf.data.remote.firebase

import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.data.safeCall
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAccountDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : RemoteAccountDao {
    override suspend fun getAll() =
        safeCall {
            fb.getAccountsPath()
                .get()
                .await()
                .children
                .mapNotNull {
                    it.getValue(FirebaseAccountModel::class.java)
                        ?.apply { key = it.key }
                }
        }

    override suspend fun updateAll(accountModels: Map<String, FirebaseAccountModel?>) {
        safeCall {
            fb.getAccountsPath()
                .updateChildren(accountModels)
                .await()
        }
    }
}