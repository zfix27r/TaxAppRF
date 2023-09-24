package com.taxapprf.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalUserEntity
import com.taxapprf.data.local.room.model.LocalUserWithAccounts
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalUserDao {
    @Query(
        "SELECT " +
                "u.email ${LocalUserEntity.EMAIL}, " +
                "u.avatar ${LocalUserEntity.AVATAR}, " +
                "u.name ${LocalUserEntity.NAME}, " +
                "u.phone ${LocalUserEntity.PHONE}, " +
                "a.id ${LocalAccountEntity.ID}, " +
                "a.user_id ${LocalAccountEntity.USER_ID}, " +
                "a.account_key ${LocalAccountEntity.ACCOUNT_KEY}, " +
                "a.is_active ${LocalAccountEntity.IS_ACTIVE} " +
                "FROM user u " +
                "LEFT JOIN account a ON a.user_id = u.id " +
                "WHERE email = :email"
    )
    fun observe(email: String): Flow<List<LocalUserWithAccounts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(localAccountEntity: LocalAccountEntity): Long

    @Delete
    fun deleteAll(localAccountEntity: LocalAccountEntity): Int
}