package com.taxapprf.data.remote.firebase.entity

import com.taxapprf.data.sync.ISyncRemoteModel

data class FirebaseReportEntity(
    override val syncAt: Long? = null,
) : ISyncRemoteModel {
    override var key: String? = null
}