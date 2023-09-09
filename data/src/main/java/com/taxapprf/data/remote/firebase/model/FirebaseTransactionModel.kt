package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.sync.SyncRemote

data class FirebaseTransactionModel(
    val name: String? = null,
    val date: String? = null,
    val type: String? = null,
    val currency: String? = null,
    val rateCBR: Double? = null,
    val sum: Double? = null,
    val tax: Double? = null,
    override val syncAt: Long? = null,
) : SyncRemote {
    override var key: String? = null
}