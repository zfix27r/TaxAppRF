package com.taxapprf.data.remote.firebase.model

import com.taxapprf.domain.account.AccountModel

data class FirebaseAccountModel(
    val name: String? = null,
    val active: Boolean? = null,
    val syncAt: Long? = null,
) {
    fun toAccountModel(key: String?): AccountModel? {
        val accountKey = key ?: return null
        val active = active ?: return null
        val syncAt = syncAt ?: 0L

        return AccountModel(
            id = 0,
            accountKey,
            active,
            isSync = true,
            isDelete = false,
            syncAt = syncAt
        )
    }
}