package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.sync.SyncRemote

data class FirebaseReportModel(
    override var key: String? = null,
    val tax: Double? = null,
    val size: Int? = null,
    override val syncAt: Long? = null,
) : SyncRemote