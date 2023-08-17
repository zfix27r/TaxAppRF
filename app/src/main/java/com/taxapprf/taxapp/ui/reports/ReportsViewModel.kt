package com.taxapprf.taxapp.ui.reports

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.report.GetReportsUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportsFromExcelUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getReportsUseCase: GetReportsUseCase,
    private val saveReportsFromExcel: SaveReportsFromExcelUseCase,
) : BaseViewModel() {
    private val _reports = MutableLiveData<List<ReportModel>>()
    val reports: LiveData<List<ReportModel>> = _reports
    fun loadReports(accountKey: String) = viewModelScope.launch(Dispatchers.IO) {
        getReportsUseCase.execute(accountKey)
            .onStart {
                println("@@@@@@@@@@@@@@@@@ 2")
                start()
            }
            .catch {
                println("@@@@@@@@@@@@@@@@@ 3")
                error(it)
            }
            .collectLatest { result ->
                println("@@@@@@@@@@@@@@@@@")
                result.exceptionOrNull()
                    ?.let { error(it) }
                    ?: run {
                        result.getOrNull()?.let { results ->
                            if (results.isNotEmpty()) _reports.postValue(results)
                            else listOf<List<AccountModel>>()
                        } ?: run { listOf<List<AccountModel>>() }
                    }

                success()
            }
    }

    fun saveReportsFromExcel(intent: Intent?) = viewModelScope.launch(Dispatchers.IO) {
        intent?.data?.path?.let { uri ->
            saveReportsFromExcel.execute(uri)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }
}