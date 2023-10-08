package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.LocalDatabase.Companion.DEFAULT_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.entity.LocalDeletedKeyEntity.Companion.TABLE_NAME
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.ACCOUNT_KEY
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.EMPTY_KEY
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.REPORT_KEY
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.TRANSACTION_KEY
import com.taxapprf.data.sync.SYNC_AT

@Entity(tableName = TABLE_NAME)
data class LocalDeletedKeyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int = DEFAULT_ID,

    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String? = EMPTY_KEY,
    @ColumnInfo(name = TRANSACTION_KEY)
    val transactionKey: String? = EMPTY_KEY,

    @ColumnInfo(name = SYNC_AT)
    val syncAt: Long,
) {
    companion object {
        const val TABLE_NAME = "deleted_key"
    }
}