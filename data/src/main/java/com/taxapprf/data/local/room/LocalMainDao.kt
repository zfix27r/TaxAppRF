package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.taxapprf.data.UserRepositoryImpl
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.entity.LocalUserEntity

@Dao
interface LocalMainDao {
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): LocalUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUserEntity(localUserEntity: LocalUserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAccountEntity(localAccountEntity: LocalAccountEntity): Long

    @Transaction
    fun saveDefaultUser(
        defaultLocalAccountName: String
    ) {
        val defaultLocalUserEntity = LocalUserEntity(
            email = UserRepositoryImpl.LOCAL_USER_EMAIL
        )
        val userId = saveUserEntity(defaultLocalUserEntity).toInt()

        val defaultLocalAccountEntity = LocalAccountEntity(
            userId = userId,
            isActive = true,
            remoteKey = defaultLocalAccountName
        )
        saveAccountEntity(defaultLocalAccountEntity)
    }

    /* < DELETE TRANSACTION */
    @Query("SELECT * FROM report WHERE id = :reportId LIMIT 1")
    fun getLocalReportEntity(reportId: Int): LocalReportEntity?

    @Query("SELECT * FROM report WHERE account_id = :accountId AND remote_key = :remoteKey LIMIT 1")
    fun getLocalReportEntity(accountId: Int, remoteKey: String): LocalReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReport(localReportEntity: LocalReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTransaction(localTransactionEntity: LocalTransactionEntity): Long

    @Delete
    fun deleteReport(localReportEntity: LocalReportEntity): Int
    /* DELETE TRANSACTION /> */
}