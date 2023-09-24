package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.getEpochTime
import com.taxapprf.data.local.room.entity.LocalAccountEntity.Companion.TABLE_NAME
import com.taxapprf.data.sync.IS_DELETE
import com.taxapprf.data.sync.IS_SYNC
import com.taxapprf.data.sync.SYNC_AT
import com.taxapprf.data.sync.SyncLocal

@Entity(tableName = TABLE_NAME)
data class LocalAccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int,

    @ColumnInfo(name = USER_ID)
    val userId: Int,

    @ColumnInfo(name = ACCOUNT_KEY)
    override val key: String = "",

    @ColumnInfo(name = IS_ACTIVE)
    val isActive: Boolean = true,

    @ColumnInfo(name = IS_SYNC)
    override val isSync: Boolean = false,
    @ColumnInfo(name = IS_DELETE)
    override val isDelete: Boolean = false,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long = getEpochTime(),
) : SyncLocal {
    companion object {
        const val TABLE_NAME = "account"

        const val ID = "id"
        const val USER_ID = "user_id"

        const val ACCOUNT_KEY = "account_key"
        const val IS_ACTIVE = "is_active"
    }
}