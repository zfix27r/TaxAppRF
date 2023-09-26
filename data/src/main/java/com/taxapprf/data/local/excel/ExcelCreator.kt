package com.taxapprf.data.local.excel

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.TransactionModel
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellRangeAddress
import java.io.File
import java.io.FileOutputStream


class ExcelCreator(
    private val context: Context,
) {
    private val workbook = HSSFWorkbook()

    fun getExcelToStorage(
        report: ReportModel,
        transactions: List<TransactionModel>,
        fileName: String,
    ): Uri {
        createWorkbook(report, transactions)

        val downloadFilePath = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .path + "/" + fileName

        val file = File(downloadFilePath)
        val outFile = FileOutputStream(file)

        workbook.write(outFile)
        outFile.close()

        return FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
    }

    fun getExcelToShare(
        report: ReportModel,
        transactions: List<TransactionModel>,
        fileName: String,
    ): Uri {
        createWorkbook(report, transactions)

        val filePath = context.filesDir.toString() + "/" + fileName

        val outFile = context.openFileOutput(fileName, Context.MODE_PRIVATE)

        workbook.write(outFile)
        outFile.close()

        return FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            File(filePath)
        )
    }

    private fun createWorkbook(
        report: ReportModel,
        transactions: List<TransactionModel>,
    ) {
        val sheet = workbook.createSheet(report.name)
        var rownum = 0
        var cell: Cell
        var row: Row
        row = sheet.createRow(rownum)

        //1
        cell = row.createCell(0, CellType.STRING)
        val s = String.format("Расчет налога за %s год", report.name)
        cell.setCellValue(s)
        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 6))
        rownum++
        row = sheet.createRow(rownum)

        //2
        cell = row.createCell(0, CellType.STRING)
        val ss = String.format("Сумма налога составляет: %.2f руб.", report.tax)
        cell.setCellValue(ss)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 6))
        rownum++
        row = sheet.createRow(rownum)

        //3.1
        cell = row.createCell(0, CellType.STRING)
        cell.setCellValue("Обозначение сделки")

        //3.2
        cell = row.createCell(1, CellType.STRING)
        cell.setCellValue("Тип операции")

        //3.3
        cell = row.createCell(2, CellType.STRING)
        cell.setCellValue("Дата")

        //3.4
        cell = row.createCell(3, CellType.STRING)
        cell.setCellValue("Сумма")

        //3.5
        cell = row.createCell(4, CellType.STRING)
        cell.setCellValue("Актив")

        //3.6
        cell = row.createCell(5, CellType.STRING)
        cell.setCellValue("Курс ЦБ РФ")

        //3.7
        cell = row.createCell(6, CellType.STRING)
        cell.setCellValue("Налог, руб.")
        for (transaction in transactions) {
            rownum++
            row = sheet.createRow(rownum)
            cell = row.createCell(0, CellType.STRING)
            cell.setCellValue(transaction.name)
            cell = row.createCell(1, CellType.NUMERIC)
            //cell.setCellValue(transaction.type)
            cell = row.createCell(2, CellType.STRING)
            cell.setCellValue(transaction.date.toString())
            cell = row.createCell(3, CellType.NUMERIC)
            cell.setCellValue(transaction.sum)
            cell = row.createCell(4, CellType.STRING)
            cell.setCellValue(transaction.currencyCharCode)
            cell = row.createCell(5, CellType.NUMERIC)
            //cell.setCellValue(transaction.currencyRate)
            cell = row.createCell(6, CellType.NUMERIC)
            //cell.setCellValue(transaction.tax)
        }
    }
}