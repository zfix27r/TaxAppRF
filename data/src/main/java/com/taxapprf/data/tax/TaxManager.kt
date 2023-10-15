package com.taxapprf.data.tax

/*
object TaxManager {
    const val RUB_TAX_BASE = 0.13
    const val RUB_TAX_LUXURY = 0.15
    const val RUB_SUM_LUXURY = 5_000_000

    lateinit var networkManager: NetworkManager
    lateinit var currencyRepository: CurrencyRepository

    private var report: IReportTaxModel? = null
    private var transactions: List<TransactionTaxRUBModel> = emptyList()

    private fun recalculateReportTransactions() {
        TODO("Not yet implemented")
    }

    suspend fun taxUp(report: IReportTaxModel, transactions: List<TransactionTaxRUBModel>) {
        if (!networkManager.isConnection) return

        val oldReportSumRUB = report.sumRUB

        updateSumRUB()

        if (report.sumRUB > RUB_SUM_LUXURY && oldReportSumRUB <= RUB_SUM_LUXURY)
            recalculateReportTransactions()
        if (report.sum >)
        val report =
    }

    private suspend fun updateSumRUB() {

    }

    private fun updateTaxRUB() {
        report?.let { report ->
            transactions.forEach { transaction ->
                calculateTaxRUB(
                    report.sumRUB,
                    transaction.sumRUB,
                    transaction.typeOrdinal
                )?.let { taxRUB ->
                    transaction.taxRUB = taxRUB
                    report.taxRUB += taxRUB
                }
            }
        }
    }






    fun taxDown(report: IReportTaxModel, transaction: TransactionTaxRUBModel) {

    }

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

        val updateTransactionTax = saveTransactionModel.rate?.let { rate ->
            calculateTaxRUB(
                saveTransactionModel.sum,
                rate,
                saveTransactionModel.transactionTypeOrdinal
            )
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

        val transaction = saveTransactionModel
            .toLocalTransactionEntity(updatedReportId.toInt(), updateTransactionTax)

        val id = localMainDao.saveTransaction(transaction).toInt()
        return if (id == 0) null else id
    }

    override suspend fun updateTransaction(updateTransactionTaxModel: UpdateTransactionTaxModel): Int? {
        val rate = updateTransactionTaxModel.rate
            ?: return null
        val transaction =
            localMainDao.getLocalTransactionEntity(updateTransactionTaxModel.transactionId)
                ?: return null
        val report = localMainDao.getLocalReportEntity(transaction.reportId)
            ?: return null

        calculateTaxRUB(transaction.sum, rate, transaction.typeOrdinal)?.let { tax ->
            return if (localMainDao.updateTax(
                    report,
                    transaction,
                    tax
                ) == 0
            ) null else transaction.id
        }

        return null
    }

    private fun LocalReportEntity.isMoveTransaction(newRemoteKey: String) =
        remoteKey != newRemoteKey

    private fun Int.isTransactionLast() =
        this < 2

    private fun LocalReportEntity.delete() =
        localMainDao.deleteReport(this)

    private fun LocalReportEntity.updateWithDeleteTransaction(transactionTax: Double?) =
        transactionTax?.let {
            val newTax = tax - transactionTax
            val newSize = size - 1
            localMainDao.saveReport(copy(tax = newTax, size = newSize))
        }

    private fun LocalReportEntity.updateWithUpdateTransaction(
        oldTransactionTax: Double?,
        updateTransactionTax: Double?
    ) =
        let {
            val oldTax = oldTransactionTax ?: LocalReportEntity.DEFAULT_TAX
            val updateTax = updateTransactionTax ?: LocalReportEntity.DEFAULT_TAX
            val newTax = tax - oldTax + updateTax
            localMainDao.saveReport(copy(tax = newTax))
        }

    private fun LocalReportEntity.updateWithAddTransaction(updateTransactionTax: Double?) =
        let {
            val updateTax = updateTransactionTax ?: LocalReportEntity.DEFAULT_TAX
            val newTax = tax + updateTax
            val newSize = size + 1
            localMainDao.saveReport(copy(tax = newTax, size = newSize))
        }

    private fun Int?.isUpdateTransaction() = this != null

    private fun addOrUpdateNewReportWithAddTransaction(
        accountId: Int,
        newReportKey: String,
        newTransactionTax: Double?
    ) =
        localMainDao.getLocalReportEntity(accountId, newReportKey)
            ?.updateWithAddTransaction(newTransactionTax)
            ?: run {
                val newLocalReportEntity = LocalReportEntity(
                    accountId = accountId,
                    remoteKey = newReportKey,
                    tax = newTransactionTax ?: LocalReportEntity.DEFAULT_TAX,
                    size = 1
                )
                localMainDao.saveReport(newLocalReportEntity)
            }

    private fun SaveTransactionModel.toLocalTransactionEntity(
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
}*/
