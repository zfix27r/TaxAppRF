package com.taxapprf.data.local.excel

import android.content.Context
import androidx.core.content.FileProvider
import com.taxapprf.data.error.DataErrorExcel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.TransactionModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExcelDaoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ExcelDao {
    override fun sendReport(report: ReportModel, transactions: List<TransactionModel>) = flow {
        val excel = ExcelCreator(context, report, transactions).create()

        if (!excel.exists()) throw DataErrorExcel()
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            excel
        )

        emit(uri)
    }
}