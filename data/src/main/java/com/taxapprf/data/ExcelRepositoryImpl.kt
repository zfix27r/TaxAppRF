package com.taxapprf.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.taxapprf.data.local.excel.ExcelCreator
import com.taxapprf.domain.ExcelRepository
import com.taxapprf.domain.excel.ExportExcelModel
import com.taxapprf.domain.excel.ImportExcelModel
import com.taxapprf.domain.main.SaveTransactionModel
import com.taxapprf.taxapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExcelRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ExcelRepository {
    override suspend fun export(exportExcelModel: ExportExcelModel): Uri? {
        with(exportExcelModel) {
            reportTitle = context.getString(R.string.excel_report_title)
            reportInfo = context.getString(R.string.excel_report_info)

            transactionTitles = listOf(
                context.getString(R.string.excel_transaction_name),
                context.getString(R.string.excel_transaction_type),
                context.getString(R.string.excel_transaction_date),
                context.getString(R.string.excel_transaction_sum),
                context.getString(R.string.excel_transaction_currency_char_code),
                context.getString(R.string.excel_transaction_currency_rate),
                context.getString(R.string.excel_transaction_tax),
            )
        }

        return ExcelCreator().createWorkbook(exportExcelModel)?.let { book ->
            val fileName = getExcelFileName()

            val downloadFilePath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .path + "/" + fileName

            val file = File(downloadFilePath)
            val outFile = FileOutputStream(file)

            book.write(outFile)
            outFile.close()

            FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                file
            )
        }
    }

    override suspend fun import(importExcelModel: ImportExcelModel): List<SaveTransactionModel> {
        TODO("Not yet implemented")
    }

    private fun getExcelFileName(): String {
        val currentDate = Date()
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        return "Statement-$dateText.xls"
    }
}