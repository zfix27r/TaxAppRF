package com.taxapprf.data

import com.taxapprf.data.error.DataErrorInternal
import com.taxapprf.data.local.room.LocalDatabase
import com.taxapprf.data.local.room.LocalMainDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.domain.MainRepository
import com.taxapprf.domain.main.SaveTransaction1Model
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val localDao: LocalMainDao
) : MainRepository {

    /*
    * 1. Получить необходимые данные отчетов
    * 2. Выполнить обновление отчетов в соответствии с операцией
    * 2.1 Перемещение - "вычесть" транзакцию из старого отчета, "зачесть" в новый отчет
    * 2.2 Добавление  - "зачесть" в новый отчет
    * 2.3 Обновление  -  вычесть налоговую базу из нового отчета
    * 3. Сохранить отчеты
    * 4. Сохранить транзаакцию
    * 5. При наличии инернета обновить удаленные данные
    * */
    override suspend fun saveTransaction(saveTransaction1Model: SaveTransaction1Model) {
        val accountId = saveTransaction1Model.accountId
        val transactionId = saveTransaction1Model.transactionId
        val reportKey = saveTransaction1Model.date.getYear()

        val report = saveTransaction1Model.reportId
            ?.let {
                localDao.getLocalReportEntity(it)
                    ?: throw DataErrorInternal()
            }
        val oldTransactionTax = saveTransaction1Model.tax

        val updateTransactionTax = saveTransaction1Model.rate?.let { rate ->
            calculateTax(saveTransaction1Model.sum, rate, saveTransaction1Model.transactionTypeOrdinal)
        }

        val updatedReportId = report?.let {
            if (report.isMoveTransaction(reportKey)) {
                if (report.size.isTransactionLast())
                    report.delete()
                else
                    report.updateWithDeleteTransaction(oldTransactionTax)

                addOrUpdateNewReportWithAddTransaction(accountId, reportKey, updateTransactionTax)
            } else {
                if (transactionId.isUpdateTransaction())
                    report.updateWithUpdateTransaction(oldTransactionTax, updateTransactionTax)
                else
                    report.updateWithAddTransaction(updateTransactionTax)
            }
        } ?: addOrUpdateNewReportWithAddTransaction(accountId, reportKey, updateTransactionTax)

        val transaction = saveTransaction1Model
            .toLocalTransactionEntity(updatedReportId.toInt(), updateTransactionTax)

        localDao.saveTransaction(transaction)
    }

    private fun LocalReportEntity.isMoveTransaction(newRemoteKey: String) =
        remoteKey != newRemoteKey

    private fun Int.isTransactionLast() =
        this < 2

    private fun LocalReportEntity.delete() =
        localDao.deleteReport(this)

    private fun LocalReportEntity.updateWithDeleteTransaction(transactionTax: Double?) =
        transactionTax?.let {
            val newTax = tax - transactionTax
            val newSize = size - 1
            localDao.saveReport(copy(tax = newTax, size = newSize))
        }

    private fun LocalReportEntity.updateWithUpdateTransaction(
        oldTransactionTax: Double?,
        updateTransactionTax: Double?
    ) =
        let {
            val oldTax = oldTransactionTax ?: DEFAULT_TAX
            val updateTax = updateTransactionTax ?: DEFAULT_TAX
            val newTax = tax - oldTax + updateTax
            localDao.saveReport(copy(tax = newTax))
        }

    private fun LocalReportEntity.updateWithAddTransaction(updateTransactionTax: Double?) =
        let {
            val updateTax = updateTransactionTax ?: DEFAULT_TAX
            val newTax = tax + updateTax
            val newSize = size + 1
            localDao.saveReport(copy(tax = newTax, size = newSize))
        }

    private fun Int?.isUpdateTransaction() = this != null

    private fun addOrUpdateNewReportWithAddTransaction(
        accountId: Int,
        newReportKey: String,
        newTransactionTax: Double?
    ) =
        localDao.getLocalReportEntity(accountId, newReportKey)
            ?.updateWithAddTransaction(newTransactionTax)
            ?: run {
                val newLocalReportEntity = LocalReportEntity(
                    accountId = accountId,
                    remoteKey = newReportKey,
                    tax = newTransactionTax ?: DEFAULT_TAX,
                )
                localDao.saveReport(newLocalReportEntity)
            }

    private fun SaveTransaction1Model.toLocalTransactionEntity(
        reportId: Int,
        tax: Double?
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
            tax = tax,
            syncAt = getEpochTime()
        )
    }

    companion object {
        const val DEFAULT_TAX = 0.0
    }
}