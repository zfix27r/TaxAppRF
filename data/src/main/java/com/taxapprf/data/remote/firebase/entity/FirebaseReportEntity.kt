package com.taxapprf.data.remote.firebase.entity

import com.taxapprf.data.sync.SyncRemote

data class FirebaseReportEntity(
    override val syncAt: Long? = null,
) : SyncRemote {
    override var key: String? = null
}