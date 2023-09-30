package com.taxapprf.taxapp.ui.reports

import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.delete.DeleteReportWithTransactionsModel
import com.taxapprf.domain.delete.DeleteReportWithTransactionsUseCase
import com.taxapprf.domain.report.ObserveReportsUseCase
import com.taxapprf.domain.excel.ImportExcelModel
import com.taxapprf.domain.excel.ImportExcelUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.makeHot
import com.taxapprf.taxapp.ui.showLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getReportsUseCase: ObserveReportsUseCase,
    private val saveReportsFromUriUseCase: ImportExcelUseCase,
    private val deleteReportWithTransactionsUseCase: DeleteReportWithTransactionsUseCase,
) : BaseViewModel() {
    fun observeReports() =
        getReportsUseCase.execute(account.id)
            .showLoading()
            .makeHot(viewModelScope)

    fun deleteReport(reportModel: ReportModel) =
        viewModelScope.launch(Dispatchers.IO) {
            val deleteReportWithTransactionsModel =
                DeleteReportWithTransactionsModel(reportModel.id)
            deleteReportWithTransactionsUseCase.execute(deleteReportWithTransactionsModel)
        }

    fun saveReportsFromExcel(intent: Intent?) =
        intent?.data?.path?.let { uri ->
            viewModelScope.launch(Dispatchers.IO) {
                val saveReportsFromUriModel = ImportExcelModel(account.id, uri)
                saveReportsFromUriUseCase.execute(saveReportsFromUriModel)
            }
        }
}