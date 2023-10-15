package com.taxapprf.data.remote.firebase.entity

import com.taxapprf.data.sync.ISyncRemoteModel

data class FirebaseAccountEntity(
    override val syncAt: Long? = null,
) : ISyncRemoteModel {
    override var key: String? = null
}