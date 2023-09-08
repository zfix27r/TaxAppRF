package com.taxapprf.domain.transaction

import com.taxapprf.domain.Sync

data class TransactionModel(
    val id: Int,
    override val key: String,
    val name: String?,
    val date: String,
    val type: String,
    val currency: String,
    val rateCBRF: Double,
    val sum: Double,
    val tax: Double,
    override val isSync: Boolean,
    override val isDelete: Boolean,
    override val syncAt: Long,
) : Sync