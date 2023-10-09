package com.taxapprf.taxapp.ui.reports

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.taxapprf.data.local.room.LocalDatabase.Companion.ACCOUNT_ID
import com.taxapprf.domain.delete.DeleteReportWithTransactionsModel
import com.taxapprf.domain.delete.DeleteReportWithTransactionsUseCase
import com.taxapprf.domain.excel.ImportExcelModel
import com.taxapprf.domain.excel.ImportExcelUseCase
import com.taxapprf.domain.report.ObserveReportsUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.showLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getReportsUseCase: ObserveReportsUseCase,
    private val saveReportsFromUriUseCase: ImportExcelUseCase,
    private val deleteReportWithTransactionsUseCase: DeleteReportWithTransactionsUseCase,
) : BaseViewModel() {
    private val accountId = savedStateHandle.get<Int>(ACCOUNT_ID)

    private val _reports: MutableStateFlow<List<ReportModel>?> = MutableStateFlow(null)
    val reports = _reports.asStateFlow()

    fun updateReports(iAccountId: Int?) =
        let {
            val id = iAccountId ?: accountId
            id?.let { accountId ->
                viewModelScope.launch(Dispatchers.IO) {
                    getReportsUseCase.execute(accountId)
                        .showLoading()
                        .collectLatest { _reports.value = it }
                }
            }
        }

    fun deleteReport(reportModel: ReportModel) =
        viewModelScope.launch(Dispatchers.IO) {
            val deleteReportWithTransactionsModel =
                DeleteReportWithTransactionsModel(reportModel.id)
            deleteReportWithTransactionsUseCase.execute(deleteReportWithTransactionsModel)
        }

    fun saveReportsFromExcel(intent: Intent?) =
        accountId?.let {
            intent?.data?.path?.let { uri ->
                viewModelScope.launch(Dispatchers.IO) {
                    val saveReportsFromUriModel = ImportExcelModel(accountId, uri)
                    saveReportsFromUriUseCase.execute(saveReportsFromUriModel)
                }
            }
        }
}