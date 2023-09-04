package com.taxapprf.data

import com.taxapprf.data.error.DataErrorCBR
import com.taxapprf.data.error.DataErrorConnection
import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.local.room.dao.LocalTransactionDao
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.data.remote.firebase.model.GetReportModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.SaveReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.ObserveTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val localTransactionDao: LocalTransactionDao,
    private val firebaseTransactionDao: FirebaseTransactionDaoImpl,
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val cbrapi: CBRAPI,
    private val excelDao: ExcelDaoImpl,
) : TransactionRepository {
    override fun observeTransactions(observeTransactionsModel: ObserveTransactionsModel) =
        channelFlow {
            val local = mutableMapOf<String, TransactionModel>()

            println("@@@@@@@@@@@ 1" + observeTransactionsModel)
            launch {
                localTransactionDao.observeAll(
                    observeTransactionsModel.accountKey,
                    observeTransactionsModel.yearKey
                ).collectLatest { transactions ->
                    local.clear()
                    send(
                        transactions.map {
                            val transaction = it.toTransactionModel()
                            local[transaction.key] = transaction
                            transaction
                        }
                    )
                    println("@@@@@@@@@@@ 2 " + transactions)
                }
            }

            launch {
                firebaseTransactionDao.observeTransactions(observeTransactionsModel)
                    .collectLatest { result ->
                        println("@@@@@@@@@@@ 3 " + result)
                        result.getOrNull()?.let { reports ->
                            local.sync(
                                reports,
                                saveLocal = {
                                    localTransactionDao.save(
                                        it.toListLocalTransactionEntity(
                                            observeTransactionsModel.accountKey,
                                            observeTransactionsModel.yearKey
                                        )
                                    )
                                },
                                deleteLocal = {
                                    localTransactionDao.delete(
                                        it.toListLocalTransactionEntity(
                                            observeTransactionsModel.accountKey,
                                            observeTransactionsModel.yearKey
                                        )
                                    )
                                },
                                saveRemote = {
                                    launch {
                                        firebaseTransactionDao.saveTransactions(
                                            observeTransactionsModel.accountKey,
                                            observeTransactionsModel.yearKey,
                                            it.toMapFirebaseTransactionModel()
                                        )
                                    }
                                }
                            )
                        }
                    }
            }
        }

    override fun saveTransaction(saveTransactionModel: SaveTransactionModel) = flow {
        saveTransactionModel.saveAndUpdatePath()
        emit(Unit)
    }

    private suspend fun SaveTransactionModel.saveAndUpdatePath() {
        updateCBRRate()
        tax = updateTax(sum, type, rateCBR)

        updatePathTransaction()

        firebaseTransactionDao.saveTransaction(this)
    }

    private suspend fun SaveTransactionModel.updatePathTransaction() {
        asDeleteTransactionModel()?.let {
            if (isReportYearChanged())
                firebaseTransactionDao.deleteTransaction(it)
        }

        incrementAndSave(accountKey, yearKey)
    }

    override fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel) = flow {
        with(deleteTransactionModel) {
            val newSize = reportSize - 1

            if (newSize == 0) {
                val deleteReportModel = DeleteReportModel(accountKey, yearKey)
                firebaseReportDao.deleteReport(deleteReportModel)
            } else {
                /*                val newTax = (reportTax - transactionTax).roundUpToTwo()

                                val saveReportModel = SaveReportModel(accountKey, yearKey, newTax, newSize)
                                firebaseReportDao.saveReport(saveReportModel)
                                firebaseTransactionDao.deleteTransaction(deleteTransactionModel)*/
            }

            emit(Unit)
        }
    }

    override fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel) = flow {
        emit(excelDao.getExcelToShare(getExcelToShareModel))
    }

    override fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel) = flow {
        emit(excelDao.getExcelToStorage(getExcelToStorageModel))
    }

    override fun saveTransactionsFromExcel(saveTransactionsFromExcelModel: SaveTransactionsFromExcelModel) =
        flow {
            excelDao.saveExcel(saveTransactionsFromExcelModel)
                .map { it.saveAndUpdatePath() }

            emit(Unit)
        }

    private suspend fun SaveTransactionModel.incrementAndSave(
        accountKey: String,
        yearKey: String
    ) {
        val getReportModel = GetReportModel(accountKey, yearKey)

        firebaseReportDao.getReport(getReportModel).let {
            val newTax = (it.tax + tax).roundUpToTwo()
            val newSize = it.size + 1
            val saveReportModel = SaveReportModel(accountKey, yearKey, newTax, newSize)
            firebaseReportDao.saveReport(saveReportModel)
        }
    }

    private fun SaveTransactionModel.updateCBRRate() {
        try {
            val request = cbrapi.getCurrency(date).execute()

            try {
                rateCBR = request.body()!!
                    .getCurrencyRate(currency)!!
                    .roundUpToTwo()
            } catch (_: Exception) {
                throw DataErrorCBR()
            }
        } catch (_: Exception) {
            throw DataErrorConnection()
        }
    }

    private fun LocalTransactionEntity.toTransactionModel() =
        TransactionModel(key, name, date, type, currency, rateCBR, sum, tax, isSync, syncAt)

    private fun List<TransactionModel>.toListLocalTransactionEntity(
        accountKey: String,
        yearKey: String
    ) = map {
        LocalTransactionEntity(
            key = it.key,
            accountKey = accountKey,
            yearKey = yearKey,
            name = it.name,
            date = it.date,
            type = it.type,
            currency = it.currency,
            rateCBR = it.rateCBR,
            sum = it.sum,
            tax = it.tax,
            isSync = it.isSync,
            syncAt = it.syncAt
        )
    }

    private fun List<TransactionModel>.toMapFirebaseTransactionModel(): MutableMap<String, FirebaseTransactionModel> {
        val accounts = mutableMapOf<String, FirebaseTransactionModel>()
        map {
            accounts.put(
                it.key, FirebaseTransactionModel(
                    name = it.name,
                    date = it.date,
                    type = it.type,
                    currency = it.currency,
                    rateCBR = it.rateCBR,
                    sum = it.sum,
                    tax = it.tax,
                    syncAt = it.syncAt
                )
            )
        }
        return accounts
    }
}