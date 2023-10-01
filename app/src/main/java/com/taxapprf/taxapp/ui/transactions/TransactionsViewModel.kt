package com.taxapprf.taxapp.ui.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.taxapprf.data.REPORT_ID
import com.taxapprf.domain.delete.DeleteTransactionWithReportModel
import com.taxapprf.domain.delete.DeleteTransactionWithReportUseCase
import com.taxapprf.domain.excel.ExportExcelModel
import com.taxapprf.domain.excel.ExportExcelUseCase
import com.taxapprf.domain.report.ObserveReportUseCase
import com.taxapprf.domain.transaction.ObserveTransactionsUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.makeHot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeReportUseCase: ObserveReportUseCase,
    private val getTransactionsUseCase: ObserveTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionWithReportUseCase,
    private val exportExcelUseCase: ExportExcelUseCase,
    private val updateTaxTransactionUseCase: UpdateReportWithTransactionTaxUseCase,
) : BaseViewModel() {
    val reportId = savedStateHandle.get<Int>(REPORT_ID)

    fun observeReport() =
        observeReportUseCase.execute(reportId)
            ?.flowOn(Dispatchers.IO)
            ?.makeHot(viewModelScope)

    fun observeTransactions() =
        getTransactionsUseCase.execute(reportId)
            ?.flowOn(Dispatchers.IO)
            ?.makeHot(viewModelScope)

    fun deleteTransaction(transactionModel: TransactionModel) =
        viewModelScope.launch(Dispatchers.IO) {
            val deleteTransactionModel = DeleteTransactionWithReportModel(transactionModel.id)
            deleteTransactionUseCase.execute(deleteTransactionModel)
        }

    fun exportReport(transactionTypes: Map<Int, String>) =
        reportId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val getExcelReportModel = ExportExcelModel(reportId, transactionTypes)
                exportExcelUseCase.execute(getExcelReportModel)?.let {
                    successExport(it)
                }
            }
        }

    fun shareReport(transactionTypes: Map<Int, String>) {
        reportId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val getExcelReportModel = ExportExcelModel(reportId, transactionTypes)
                exportExcelUseCase.execute(getExcelReportModel)?.let {
                    successShare(it)
                }
            }
        }
    }

    private fun List<TransactionModel>.updateTax() {
        map { transition ->
            if (transition.currencyRate == null)
                viewModelScope.launch(Dispatchers.IO) {
                    val update = UpdateReportWithTransactionTaxModel(
                        reportId = reportId,
                        transactionId = transition.id,
                        type = transition.typeK,
                        currencyId = transition.currencyId,
                        date = transition.date,
                        sum = transition.sum,
                        oldTax = transition.tax
                    )
                    updateTaxTransactionUseCase.execute(update)
                }
        }
    }
}