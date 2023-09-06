package com.taxapprf.data

import com.taxapprf.data.error.DataErrorInternal
import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.local.room.dao.LocalTransactionDao
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.data.sync.SyncTransactions
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.ObserveTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager,
    private val localTransactionDao: LocalTransactionDao,
    private val remoteTransactionDao: FirebaseTransactionDaoImpl,
    private val reportRepository: ReportRepositoryImpl,
    private val currencyRepository: CurrencyRepositoryImpl,
    private val excelDao: ExcelDaoImpl,
) : TransactionRepository {
    override fun observe(observeTransactionsModel: ObserveTransactionsModel) =
        SyncTransactions(
            localTransactionDao,
            remoteTransactionDao,
            observeTransactionsModel.accountKey,
            observeTransactionsModel.reportKey
        ).observe()

    override fun save(saveTransactionModel: SaveTransactionModel) = flow {
        with(saveTransactionModel) {
            try {
                val newReportKey = date.split("/")[2]

                updateOrDeleteOldReport(newReportKey)

                var transactionCBRRate = 0.0
                var transactionTax = 0.0

                if (networkManager.available) {
                    transactionCBRRate = currencyRepository.getCurrencyRate(date, currency)
                    transactionTax = (sum * transactionCBRRate).roundUpToTwo()
                }

                var newReportTax = transactionTax
                var newReportSize = 1

                reportRepository.get(accountKey, newReportKey)?.let {
                    newReportTax += it.tax
                    newReportSize += it.size
                }

                val saveReportModel =
                    SaveReportModel(accountKey, newReportKey, newReportTax, newReportSize)
                reportRepository.save(saveReportModel)

                if (networkManager.available) {
                    remoteTransactionDao.saveAll(
                        accountKey,
                        newReportKey,
                        transactionKey,
                        toFirebaseTransactionModel()
                    )
                } else {
//                    localTransactionDao.save(toLocalTransactionEntity())
                }
            } catch (_: Exception) {
                throw DataErrorInternal()
            }
        }
        emit(Unit)
    }

    private fun SaveTransactionModel.updateOrDeleteOldReport(newReportKey: String) {
        transactionKey?.let {
            reportKey?.let { reportKey ->
                if (newReportKey != reportKey) {
                    reportRepository.get(accountKey, reportKey)?.let {
                        val size = it.size - 1
                        if (size == 0) {
                            reportRepository.deleteWithTransactions(accountKey, reportKey)
                        } else {
                            val tax = it.tax - (tax ?: 0.0)
                            val saveReportModel =
                                SaveReportModel(accountKey, reportKey, tax, size)
                            reportRepository.save(saveReportModel)
                        }
                    }
                }
            }
        }
    }

    private suspend fun ReportModel.deleteTransactionInOldReport() {

        // локально
        // обновить репорт старый = уменьшить на 1 размер и вычесть налог
        // обновить репорт новый = прибавить на 1 размер и прибавить налог
        // обновить запись

        // удаленно
        // если отчет старый пустой = удалить, иначе уменьшить на 1 и уменьшить налог
        // удалить запись из отчета
        // новый отчет = увеличить на 1 и прибавить налог
        // добавить запись в отчет

    }

    override fun delete(deleteTransactionModel: DeleteTransactionModel) = flow {
        with(deleteTransactionModel) {
            val tax = reportTax - transactionTax
            val size = reportSize - 1

            if (networkManager.available)
                remoteTransactionDao.deleteAll(accountKey, reportKey, transactionKey)
            else
                localTransactionDao.deleteDeferred(transactionKey)

            updateOrDeleteReport(accountKey, reportKey, tax, size)
        }

        emit(Unit)
    }

    private suspend fun updateOrDeleteReport(
        accountKey: String,
        reportKey: String,
        tax: Double,
        size: Int
    ) {
        if (size == 0) {
            reportRepository.deleteWithTransactions(accountKey, reportKey)
        } else {
            val saveReportModel =
                SaveReportModel(accountKey, reportKey, tax, size)
            reportRepository.save(saveReportModel)
        }
    }

    override fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel) = flow {
        emit(excelDao.getExcelToShare(getExcelToShareModel))
    }

    override fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel) = flow {
        emit(excelDao.getExcelToStorage(getExcelToStorageModel))
    }

    override fun saveFromExcel(saveTransactionsFromExcelModel: SaveTransactionsFromExcelModel) =
        flow {
            excelDao.saveExcel(saveTransactionsFromExcelModel)
                .map { save(it) }

            emit(Unit)
        }

    private fun SaveTransactionModel.toFirebaseTransactionModel() =
        FirebaseTransactionModel(name, date, type, currency, rateCBR, sum, tax, getTime())

}