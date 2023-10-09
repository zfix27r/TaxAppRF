package com.taxapprf.data.remote.firebase.entity

import com.taxapprf.data.sync.SyncRemote

data class FirebaseTransactionEntity(
    val name: String? = null,
    val date: Long? = null,
    val type: String? = null,
    val currency: String? = null,
    val rateCBR: Double? = null,
    val sum: Double? = null,
    val tax: Double? = null,
    override val syncAt: Long? = null,
) : SyncRemote {
    override var key: String? = null
}