package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.error.DataErrorResponseEmpty
import com.taxapprf.domain.account.AccountModel

data class FirebaseAccountModel(
    val name: String? = null,
    val active: Boolean? = null
) {
    fun toAccountModel(key: String?) = AccountModel(
        name = key ?: throw DataErrorResponseEmpty(),
        active = active ?: throw DataErrorResponseEmpty()
    )
}