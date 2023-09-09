package com.taxapprf.taxapp.ui.reports

import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.report.DeleteReportWithTransactionsUseCase
import com.taxapprf.domain.report.ObserveReportsUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getReportsUseCase: ObserveReportsUseCase,
    private val saveReportsFromUriUseCase: SaveTransactionsFromExcelUseCase,
    private val deleteReportUseCase: DeleteReportWithTransactionsUseCase,
) : BaseViewModel() {
    private var reports: List<ReportModel>? = null
    var deleteReport: ReportModel? = null

    fun observeReports() =
        getReportsUseCase.execute(account.accountKey)
            .onStart { start() }
            .catch { error(it) }
            .onEach {
                success()
                reports = it
            }
            .flowOn(Dispatchers.IO)

    fun deleteReport() = viewModelScope.launch(Dispatchers.IO) {
        deleteReport?.let { report ->
            deleteReport = null
            flow {
                emit(
                    deleteReportUseCase.execute(
                        report.id,
                        account.accountKey,
                        report.reportKey
                    )
                )
            }
                .onStart { start() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }

    fun onSwipedReport(position: Int) {
        deleteReport = reports?.get(position)
    }

    fun saveReportsFromExcel(intent: Intent?) = viewModelScope.launch(Dispatchers.IO) {
        intent?.data?.path?.let { uri ->
            val saveReportsFromUriModel = SaveTransactionsFromExcelModel(account.accountKey, uri)
            saveReportsFromUriUseCase.execute(saveReportsFromUriModel)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }
}