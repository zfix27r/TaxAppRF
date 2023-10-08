package com.taxapprf.data

import com.taxapprf.data.error.DataErrorInternal
import com.taxapprf.data.local.room.LocalTransactionsDao
import com.taxapprf.data.local.room.entity.LocalDeletedKeyEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.domain.TransactionsRepository
import javax.inject.Inject


class TransactionsRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager,

    private val localDao: LocalTransactionsDao,

    private val remoteReportDao: RemoteReportDao,
    private val remoteTransactionDao: RemoteTransactionDao,
) : TransactionsRepository {

    /*
    * 1. Получить модели отчета и транзакций
    * 2. Обновить или удалить отчет включавший транзакцию
    * 3. Удалить записи транзакций из локальной БД
    * 4. При наличии интернета выполнить синхронизацию удаленных транзакций и отчета
    * */
    override suspend fun deleteTransactions(transactionsId: List<Int>) {
        val firstTransactionId = transactionsId.firstOrNull()
            ?: throw DataErrorInternal()
        val account = localDao.getLocalAccountEntity(firstTransactionId)
            ?: throw DataErrorInternal()
        val report = localDao.getLocalReportEntity(firstTransactionId)
            ?: throw DataErrorInternal()
        val transactions = localDao.getLocalTransactionEntities(transactionsId)

        var newSize = report.size
        var newTax = report.tax

        transactions.forEach { localTransactionEntity ->
            localTransactionEntity.tax?.let { newTax -= it }
            --newSize
        }

        if (newSize > 1) localDao.saveReport(report.copy(tax = newTax, size = newSize))
        else localDao.deleteReport(report)

        localDao.deleteTransactions(transactions)

        if (networkManager.available) {
            remoteReportDao.updateAll(report.remoteKey, report.toMapFirebaseReportModel())

            val remoteTransactionMap = mutableMapOf<String, FirebaseTransactionModel?>()

            transactions.forEach { transaction ->
                transaction.remoteKey?.let { remoteTransactionMap[it] = null }
            }

            remoteTransactionDao.updateAll(
                account.remoteKey,
                report.remoteKey,
                remoteTransactionMap
            )
        } else {
            val deletedKeys = mutableListOf<LocalDeletedKeyEntity>()

            transactions.forEach { transaction ->
                val deletedKey = LocalDeletedKeyEntity(
                    accountKey = account.remoteKey,
                    reportKey = report.remoteKey,
                    transactionKey = transaction.remoteKey,
                    syncAt = transaction.syncAt
                )
                deletedKeys.add(deletedKey)
            }

            localDao.saveDeletedKeys(deletedKeys)
        }
    }

    private fun LocalReportEntity.toMapFirebaseReportModel() =
        mapOf(
            remoteKey to FirebaseReportModel(tax, size, syncAt).apply { key = remoteKey }
        )
}
