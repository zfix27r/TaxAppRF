package com.taxapprf.data.local.excel

import android.content.Context
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.TransactionModel
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellRangeAddress
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExcelCreator (
    private val context: Context,
    private val report: ReportModel,
    private val transactions: List<TransactionModel>
) {

    fun create(): File {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet(report.year)
        var rownum = 0
        var cell: Cell
        var row: Row
        row = sheet.createRow(rownum)

        //1
        cell = row.createCell(0, CellType.STRING)
        val s = String.format("Расчет налога за %s год", report.year)
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
            cell = row.createCell(1, CellType.STRING)
            cell.setCellValue(transaction.type)
            cell = row.createCell(2, CellType.STRING)
            cell.setCellValue(transaction.date)
            cell = row.createCell(3, CellType.NUMERIC)
            cell.setCellValue(transaction.sum)
            cell = row.createCell(4, CellType.STRING)
            cell.setCellValue(transaction.currency)
            cell = row.createCell(5, CellType.NUMERIC)
            cell.setCellValue(transaction.rateCBR)
            cell = row.createCell(6, CellType.NUMERIC)
            cell.setCellValue(transaction.tax)
        }
        val currentDate = Date()
        val dateFormat: DateFormat =
            SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        val statementName = "Statement-$dateText.xls"
        val filePath = context.filesDir.toString() + "/" + statementName
        val outFile =
            context.openFileOutput(statementName, Context.MODE_PRIVATE)
        workbook.write(outFile)
        outFile.close()
        return File(filePath)
    }
}