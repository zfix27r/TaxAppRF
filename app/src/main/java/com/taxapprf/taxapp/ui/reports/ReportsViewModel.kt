package com.taxapprf.taxapp.ui.reports

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.DeleteReportUseCase
import com.taxapprf.domain.report.ObserveReportsModel
import com.taxapprf.domain.report.ObserveReportsUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getReportsUseCase: ObserveReportsUseCase,
    private val saveReportsFromUriUseCase: SaveTransactionsFromExcelUseCase,
    private val deleteReportUseCase: DeleteReportUseCase,
) : BaseViewModel() {
    private val _reports = MutableLiveData<List<ReportModel>>()
    val reports: LiveData<List<ReportModel>> = _reports

    var deleteReport: ReportModel? = null

    fun loadReports() = viewModelScope.launch(Dispatchers.IO) {
        val getReportsModel = ObserveReportsModel(account.key)
        getReportsUseCase.execute(getReportsModel)
            .onStart { start() }
            .catch { error(it) }
            .collectLatest {
                _reports.postValue(it)
                success()
            }
    }

    fun deleteReport() = viewModelScope.launch(Dispatchers.IO) {
        deleteReport?.let { report ->
            val deleteReportModel =
                DeleteReportModel(
                    accountKey = account.key,
                    reportKey = report.key,
                )

            deleteReport = null

            deleteReportUseCase.execute(deleteReportModel)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }

    fun onSwipedReport(position: Int) {
        deleteReport = _reports.value?.get(position)
    }

    fun saveReportsFromExcel(intent: Intent?) = viewModelScope.launch(Dispatchers.IO) {
        intent?.data?.path?.let { uri ->
            val saveReportsFromUriModel = SaveTransactionsFromExcelModel(account.key, uri)
            saveReportsFromUriUseCase.execute(saveReportsFromUriModel)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }
}