package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.LocalDeletedKeyEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class LocalDeletedKeyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int = DEFAULT_ID,

    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String?,
    @ColumnInfo(name = TRANSACTION_KEY)
    val transactionKey: String?,

    @ColumnInfo(name = SYNC_AT)
    val syncAt: Long,
) {
    companion object {
        const val TABLE_NAME = "deleted_key"

        const val ID = "id"

        const val ACCOUNT_KEY = "account_key"
        const val REPORT_KEY = "report_key"
        const val TRANSACTION_KEY = "transaction_key"

        const val SYNC_AT = "sync_at"

        const val DEFAULT_ID = 0
    }
}