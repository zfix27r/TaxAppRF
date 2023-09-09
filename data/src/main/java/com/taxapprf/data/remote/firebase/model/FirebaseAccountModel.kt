package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.sync.SyncRemote

data class FirebaseAccountModel(
    override var key: String? = null,
    val isActive: Boolean? = null,
    override val syncAt: Long? = null,
) : SyncRemote