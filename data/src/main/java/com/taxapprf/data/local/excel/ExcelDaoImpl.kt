package com.taxapprf.data.local.excel

import android.content.Context
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExcelDaoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ExcelDao {
    private fun getExcelFileName(): String {
        val currentDate = Date()
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        return "Statement-$dateText.xls"
    }

    override suspend fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel) =
        with(getExcelToShareModel) {
            ExcelCreator(context).getExcelToShare(report, transactions, getExcelFileName())
        }

    override suspend fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel) =
        with(getExcelToStorageModel) {
            ExcelCreator(context).getExcelToStorage(report, transactions, getExcelFileName())
        }
}