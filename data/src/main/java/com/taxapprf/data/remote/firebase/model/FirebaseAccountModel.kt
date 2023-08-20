package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.error.external.DataErrorExternalEmpty
import com.taxapprf.domain.account.AccountModel

data class FirebaseAccountModel(
    val name: String? = null,
    val active: Boolean? = null
) {
    fun toAccountModel(key: String?) = AccountModel(
        name = key ?: throw DataErrorExternalEmpty(),
        active = active ?: throw DataErrorExternalEmpty()
    )
}