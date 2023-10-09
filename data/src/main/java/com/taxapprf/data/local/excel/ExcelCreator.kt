package com.taxapprf.data.local.excel

import com.taxapprf.domain.excel.ExportExcelModel
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddress
import kotlin.properties.Delegates


class ExcelCreator {
    private var rowN by Delegates.notNull<Int>()
    private var cellN by Delegates.notNull<Int>()

    private lateinit var book: HSSFWorkbook
    private lateinit var sheet: Sheet
    private lateinit var row: Row
    private lateinit var cell: Cell

    private fun initBook() {
        rowN = START_ROW_NUM
        cellN = START_ROW_NUM

        book = HSSFWorkbook()
    }

    private fun nextSheet(title: String) {
        sheet = book.createSheet(title)
        row = sheet.createRow(rowN)
    }

    private fun nextRow() {
        rowN++
        cellN = START_COLUMN_NUM - 1

        row = sheet.createRow(rowN)
    }

    private fun nextColumn(content: String) {
        cellN++
        cell = row.createCell(cellN, CellType.STRING)
        cell.setCellValue(content)
    }

    private fun nextColumn(content: Double) {
        cellN++
        cell = row.createCell(cellN, CellType.NUMERIC)
        cell.setCellValue(content)
    }

    private fun mergeAllColumnCurrentRow() {
        sheet.addMergedRegion(CellRangeAddress(rowN, rowN, START_COLUMN_NUM, MAX_COLUMN))
    }

    fun createWorkbook(exportExcelModel: ExportExcelModel): HSSFWorkbook? {
        val reportName = exportExcelModel.reportName ?: return null
        val reportTax = exportExcelModel.reportTax ?: return null

        val reportTitle = exportExcelModel.reportTitle ?: return null
        val reportInfo = exportExcelModel.reportInfo ?: return null

        initBook()

        nextSheet(reportName)

        /* 1 */
        nextRow()
        nextColumn(String.format(reportTitle, reportName))
        mergeAllColumnCurrentRow()

        /* 2 */
        nextRow()
        nextColumn(String.format(reportInfo, reportTax))
        mergeAllColumnCurrentRow()

        /* 3 */
        nextRow()
        exportExcelModel.transactionTitles.forEach {
            nextColumn(it)
        }

        /* Transactions */
        exportExcelModel.transactions.forEach {
            nextRow()
            nextColumn(it.name ?: EMPTY_STRING)
            nextColumn(it.typeName)
            nextColumn(it.appDate)
            nextColumn(it.sum)
            nextColumn(it.currencyCharCode)
            nextColumn(it.currencyRate ?: EMPTY_DOUBLE)
            nextColumn(it.tax ?: EMPTY_DOUBLE)
        }

        return book
    }

    companion object {
        const val EMPTY_STRING = ""
        const val EMPTY_DOUBLE = 0.0
        const val MAX_COLUMN = 6
        const val START_ROW_NUM = 0
        const val START_COLUMN_NUM = 0
    }
}