package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.REPORT_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TRANSACTION_ID
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.ACCOUNT_KEY
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.REPORT_KEY
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.TRANSACTION_KEY
import com.taxapprf.data.sync.SYNC_AT

data class GetKeysTransaction(
    @ColumnInfo(name = TRANSACTION_ID)
    val id: Int,

    @ColumnInfo(name = REPORT_ID)
    val reportId: Int?,

    @ColumnInfo(name = TAX)
    val tax: Double?,

    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String?,
    @ColumnInfo(name = TRANSACTION_KEY)
    val transactionKey: String?,

    @ColumnInfo(name = SYNC_AT)
    val syncAt: Long,
)