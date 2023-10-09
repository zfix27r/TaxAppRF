package com.taxapprf.data.remote.firebase

import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.data.remote.firebase.entity.FirebaseAccountEntity
import com.taxapprf.data.safeCall
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAccountDaoImpl @Inject constructor(
    private val fb: Firebase,
) : RemoteAccountDao {
    override suspend fun getAll() =
        safeCall {
            fb.getAccountsPath()
                .get()
                .await()
                .children
                .mapNotNull {
                    it.getValue(FirebaseAccountEntity::class.java)
                        ?.apply { key = it.key }
                }
        }

    override suspend fun updateAll(accountModels: Map<String, FirebaseAccountEntity?>) {
        safeCall {
            fb.getAccountsPath()
                .updateChildren(accountModels)
                .await()
        }
    }
}