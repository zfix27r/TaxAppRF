package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.taxapprf.data.MainRepositoryImpl.Companion.LOCAL_USER_EMAIL
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.entity.LocalUserEntity
import com.taxapprf.data.local.room.model.UserDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalMainDao {
    @Query(
        "SELECT " +
                "u.email ${LocalUserEntity.EMAIL}, " +
                "u.avatar ${LocalUserEntity.AVATAR}, " +
                "u.name ${LocalDatabase.NAME}, " +
                "u.phone ${LocalUserEntity.PHONE}, " +
                "a.id ${LocalDatabase.ACCOUNT_ID}, " +
                "a.user_id ${LocalDatabase.USER_ID}, " +
                "a.remote_key ${UserDataModel.ACCOUNT_NAME}, " +
                "a.is_active ${LocalAccountEntity.IS_ACTIVE} " +
                "FROM user u " +
                "LEFT JOIN account a ON a.user_id = u.id " +
                "WHERE email = :email"
    )
    fun observeUsers(email: String): Flow<List<UserDataModel>>

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
            email = LOCAL_USER_EMAIL
        )
        val userId = saveUserEntity(defaultLocalUserEntity).toInt()

        val defaultLocalAccountEntity = LocalAccountEntity(
            userId = userId,
            isActive = true,
            remoteKey = defaultLocalAccountName
        )
        saveAccountEntity(defaultLocalAccountEntity)
    }

    /* ACCOUNTS */
    @Query("UPDATE account SET is_active = 0 WHERE is_active = 1")
    fun resetActiveAccount(): Int

    @Query("UPDATE account SET is_active = 1 WHERE remote_key = :accountName")
    fun setActiveAccount(accountName: String): Int

    @Transaction
    fun updateActiveAccount(accountName: String): Int {
        resetActiveAccount()
        return setActiveAccount(accountName)
    }

    /* SAVE TRANSACTION */
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

    /* UPDATE TRANSACTION */
    @Query("SELECT * FROM `transaction` WHERE id = :transactionId LIMIT 1")
    fun getLocalTransactionEntity(transactionId: Int): LocalTransactionEntity?

    @Update
    fun updateReport(localReportEntity: LocalReportEntity): Int

    @Update
    fun updateTransaction(localTransactionEntity: LocalTransactionEntity): Int

    @Transaction
    fun updateTax(
        localReportEntity: LocalReportEntity,
        localTransactionEntity: LocalTransactionEntity,
        newTax: Double
    ): Int {
        updateReport(localReportEntity.copy(taxRUB = localReportEntity.taxRUB + newTax))
        return updateTransaction(localTransactionEntity.copy(taxRUB = newTax))
    }

    /* SIGN OUT */
    @Query("DELETE FROM user")
    fun deleteAllUsers()

    @Query("DELETE FROM account")
    fun deleteAllAccounts()

    @Query("DELETE FROM report")
    fun deleteAllReports()

    @Query("DELETE FROM `transaction`")
    fun deleteAllTransactions()

    @Transaction
    fun deleteAll() {
        deleteAllAccounts()
        deleteAllUsers()
        deleteAllReports()
        deleteAllTransactions()
    }
}