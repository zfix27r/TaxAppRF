package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.sync.SyncRemote

data class FirebaseAccountModel(
    val active: Boolean? = null,
    override val syncAt: Long? = null,
) : SyncRemote {
    override var key: String? = null
}