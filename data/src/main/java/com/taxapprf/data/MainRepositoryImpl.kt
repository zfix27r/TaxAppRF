package com.taxapprf.data

import android.net.Uri
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.taxapprf.data.error.DataErrorInternal
import com.taxapprf.data.local.room.LocalDatabase
import com.taxapprf.data.local.room.LocalMainDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.DEFAULT_TAX_RUB
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.remote.firebase.dao.RemoteUserDao
import com.taxapprf.domain.MainRepository
import com.taxapprf.domain.main.account.AccountModel
import com.taxapprf.domain.main.account.SwitchAccountModel
import com.taxapprf.domain.main.transaction.SaveTransactionModel
import com.taxapprf.domain.main.user.ObserveUserWithAccountsModel
import com.taxapprf.domain.main.user.SignInModel
import com.taxapprf.domain.main.user.SignUpModel
import com.taxapprf.domain.main.user.UserModel
import com.taxapprf.domain.main.user.UserWithAccountsModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val localMainDao: LocalMainDao,
    private val remoteUserDao: RemoteUserDao,
) : MainRepository {
    override fun observeUserWithAccounts(
        observeUserWithAccountsModel: ObserveUserWithAccountsModel
    ) =
        findUserEmail(observeUserWithAccountsModel.defaultAccountName).let { email ->
            localMainDao.observeUsers(email)
                .map { user ->
                    UserWithAccountsModel(
                        user = user.firstOrNull()?.toUserModel(),
                        activeAccount = user.find { it.isAccountActive }
                            ?.toAccountModel(),
                        otherAccounts = user.filter { !it.isAccountActive }
                            .map { it.toAccountModel() }
                    )
                }
        }

    override suspend fun saveUser(userModel: UserModel) {
        userModel.name?.let { remoteUserDao.updateUser(it) }
    }

    override suspend fun signIn(signInModel: SignInModel) {
        remoteUserDao.signInWithEmailAndPassword(signInModel)
        updateLocalUser()
    }

    override suspend fun signUp(signUpModel: SignUpModel) {
        remoteUserDao.signUpWithEmailAndPassword(signUpModel)
        updateLocalUser()
    }

    override suspend fun signOut() {
        remoteUserDao.signOut()
        localMainDao.deleteAll()
    }

    override suspend fun switchAccount(switchAccountModel: SwitchAccountModel) {
        if (localMainDao.updateActiveAccount(switchAccountModel.accountName) == 0) {
            val newLocalAccountEntity = LocalAccountEntity(
                userId = switchAccountModel.userId,
                isActive = true,
                remoteKey = switchAccountModel.accountName
            )
            localMainDao.saveAccountEntity(newLocalAccountEntity)
        }
    }

    private fun findUserEmail(defaultAccountName: String) =
        remoteUserDao.getUser()?.email?.let { remoteUserEmail ->
            localMainDao.getUserByEmail(remoteUserEmail)?.email
                ?: run {
                    remoteUserDao.signOut()
                    getLocalUserEmail(defaultAccountName)
                }
        } ?: getLocalUserEmail(defaultAccountName)

    private fun getLocalUserEmail(defaultAccountName: String) =
        localMainDao.getUserByEmail(LOCAL_USER_EMAIL)?.let {
            LOCAL_USER_EMAIL
        } ?: run {
            localMainDao.saveDefaultUser(defaultAccountName)
            LOCAL_USER_EMAIL
        }

    private fun updateLocalUser() {
        localMainDao.getUserByEmail(LOCAL_USER_EMAIL)?.let { localUser ->
            remoteUserDao.getUser()?.let { firebaseUser ->
                val updatedLocalUserEntity =
                    localUser.copy(
                        email = firebaseUser.email,
                        avatar = firebaseUser.photoUrl?.path,
                        name = firebaseUser.displayName,
                        phone = firebaseUser.phoneNumber
                    )
                localMainDao.saveUserEntity(updatedLocalUserEntity)
            }
        }
    }

    private fun com.taxapprf.data.local.room.model.UserDataModel.toUserModel() =
        UserModel(userId, email, avatar?.let { Uri.parse(it) }, name, phone)

    private fun com.taxapprf.data.local.room.model.UserDataModel.toAccountModel() =
        AccountModel(accountId, accountName)

    /* SAVE TRANSACTIONS */
    override suspend fun saveTransaction(saveTransactionModel: SaveTransactionModel): Int? {
        val accountId = saveTransactionModel.accountId
        val transactionId = saveTransactionModel.transactionId
        val reportKey = saveTransactionModel.date.getYear()

        val report = saveTransactionModel.reportId
            ?.let {
                localMainDao.getLocalReportEntity(it)
                    ?: throw DataErrorInternal()
            }
        val oldTransactionTax = saveTransactionModel.tax

        val updatedReportId = report?.let {
            if (report.isMoveTransaction(reportKey)) {
                if (report.size.isTransactionLast())
                    report.delete()
                else
                    report.updateWithDeleteTransaction(oldTransactionTax)

                addOrUpdateNewReportWithAddTransaction(accountId, reportKey)
            } else {
                if (transactionId.isUpdateTransaction())
                    report.updateWithUpdateTransaction(oldTransactionTax)
                else
                    report.updateWithAddTransaction()
            }
        } ?: addOrUpdateNewReportWithAddTransaction(accountId, reportKey)

        val transaction = saveTransactionModel
            .toLocalTransactionEntity(updatedReportId.toInt())

        val id = localMainDao.saveTransaction(transaction).toInt()
        return if (id == 0) null else id
    }

    override fun startFirebaseAnalytics() {
        Firebase.analytics
    }

    private fun LocalReportEntity.isMoveTransaction(newRemoteKey: String) =
        remoteKey != newRemoteKey

    private fun Int.isTransactionLast() =
        this < 2

    private fun LocalReportEntity.delete() =
        localMainDao.deleteReport(this)

    private fun LocalReportEntity.updateWithDeleteTransaction(transactionTaxRUB: Double?) =
        transactionTaxRUB?.let {
            val newTaxRUB = taxRUB - transactionTaxRUB
            val newSize = size - 1
            localMainDao.saveReport(copy(taxRUB = newTaxRUB, size = newSize))
        }

    private fun LocalReportEntity.updateWithUpdateTransaction(oldTransactionTaxRUB: Double?) =
        let {
            val oldTaxRUB = oldTransactionTaxRUB ?: DEFAULT_TAX_RUB
            val newTaxRUB = taxRUB - oldTaxRUB
            localMainDao.saveReport(copy(taxRUB = newTaxRUB))
        }

    private fun LocalReportEntity.updateWithAddTransaction(updateTransactionTaxRUB: Double? = null) =
        let {
            val updateTaxRUB = updateTransactionTaxRUB ?: DEFAULT_TAX_RUB
            val newTaxRUB = taxRUB + updateTaxRUB
            val newSize = size + 1
            localMainDao.saveReport(copy(taxRUB = newTaxRUB, size = newSize))
        }

    private fun Int?.isUpdateTransaction() = this != null

    private fun addOrUpdateNewReportWithAddTransaction(
        accountId: Int,
        newReportKey: String
    ) =
        localMainDao.getLocalReportEntity(accountId, newReportKey)
            ?.updateWithAddTransaction()
            ?: run {
                val newLocalReportEntity = LocalReportEntity(
                    accountId = accountId,
                    remoteKey = newReportKey,
                    taxRUB = DEFAULT_TAX_RUB,
                    size = 1
                )
                localMainDao.saveReport(newLocalReportEntity)
            }

    private fun SaveTransactionModel.toLocalTransactionEntity(
        reportId: Int
    ): LocalTransactionEntity {
        val transactionId = transactionId ?: LocalDatabase.DEFAULT_ID

        return LocalTransactionEntity(
            id = transactionId,
            reportId = reportId,
            typeOrdinal = transactionTypeOrdinal,
            currencyOrdinal = currencyOrdinal,
            name = name,
            date = date,
            sum = sum,
            syncAt = getEpochTime()
        )
    }

    companion object {
        const val LOCAL_USER_EMAIL = "local"
    }
}