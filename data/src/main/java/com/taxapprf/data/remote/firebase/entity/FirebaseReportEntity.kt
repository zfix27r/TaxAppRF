package com.taxapprf.data.remote.firebase.entity

import com.taxapprf.data.sync.SyncRemote

data class FirebaseReportEntity(
    val tax: Double? = null,
    val size: Int? = null,
    override val syncAt: Long? = null,
) : SyncRemote {
    override var key: String? = null
}