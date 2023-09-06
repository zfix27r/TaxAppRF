package com.taxapprf.domain.transaction

import com.taxapprf.domain.Sync

data class TransactionModel(
    override val key: String,
    val name: String?,
    val date: String,
    val type: String,
    val currency: String,
    val rateCBR: Double,
    val sum: Double,
    val tax: Double,
    override val isSync: Boolean,
    override val isDeferredDelete: Boolean,
    override val syncAt: Long,
) : Sync