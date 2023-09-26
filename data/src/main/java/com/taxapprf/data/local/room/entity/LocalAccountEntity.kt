package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.getEpochTime
import com.taxapprf.data.local.room.entity.LocalAccountEntity.Companion.TABLE_NAME
import com.taxapprf.data.sync.REMOTE_KEY
import com.taxapprf.data.sync.SYNC_AT
import com.taxapprf.data.sync.SyncLocal

@Entity(tableName = TABLE_NAME)
data class LocalAccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int = DEFAULT_ID,

    @ColumnInfo(name = USER_ID)
    val userId: Int,

    @ColumnInfo(name = IS_ACTIVE)
    val isActive: Boolean,

    @ColumnInfo(name = REMOTE_KEY)
    override val remoteKey: String,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long = getEpochTime(),
) : SyncLocal {
    companion object {
        const val TABLE_NAME = "account"

        const val ID = "id"
        const val USER_ID = "user_id"

        const val IS_ACTIVE = "is_active"

        const val DEFAULT_ID = 0
    }
}