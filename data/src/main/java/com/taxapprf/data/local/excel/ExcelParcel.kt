package com.taxapprf.data.local.excel

import com.taxapprf.domain.transaction.SaveTransactionModel
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.io.File
import java.io.FileInputStream
import java.text.DateFormat
import java.text.SimpleDateFormat

class ExcelParcel(private val filePath: String) {

    fun parse(): List<SaveTransactionModel> {
        if (filePath.isEmpty()) throw Exception() // TODO обработка ошибок

        val transactions = mutableListOf<SaveTransactionModel>()

        val path = if (filePath.contains("raw:")) filePath.replaceFirst(".+raw:".toRegex(), "")
        else filePath

        val fileInputStream = FileInputStream(File(path))

        val workBook = HSSFWorkbook(fileInputStream)

        val sheet: Sheet = workBook.getSheetAt(0)
        val rowIterator: Iterator<Row> = sheet.iterator()
        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val cells: Iterator<Cell> = row.iterator()
            val transaction = SaveTransactionModel()

            try {
                for (i in 0..4) {
                    val cell = cells.next()
                    when (i) {
                        0 -> transaction.id = cell.stringCellValue
                        1 -> transaction.type = cell.stringCellValue
                        2 -> transaction.date = cell.parseDate()
                        3 -> transaction.sum = cell.numericCellValue
                        4 -> transaction.currency = cell.stringCellValue
                    }
                }
                transactions.add(transaction)
            } catch (e: Exception) {
                // TODO() недописана обработка ошибок
            }
        }
        return transactions
    }

    private fun Cell.parseDate(): String {
        return if (cellType == CellType.NUMERIC) {
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
            dateFormat.format(dateCellValue)
        } else {
            //TODO пока не ясно что тут с датой

            /*            val date = stringCellValue.replace("\\.".toRegex(), "/")
                val p = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((?:19|20)[0-9][0-9])")
                val matcher = p.matcher(date)
                if (matcher.find()) {
                    year = matcher.group(3)
                    check = true
                } else {
                    check = false
                }*/
            ""
        }
    }
}