package com.taxapprf.data.local.excel

import android.os.Environment
import com.taxapprf.data.error.DataErrorInternal
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
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
import java.util.Locale


class ExcelParcel(private val saveExcelToFirebaseModel: SaveTransactionsFromExcelModel) {
    fun parse(): List<SaveTransactionModel> {
        if (saveExcelToFirebaseModel.filePath.isEmpty()) throw DataErrorInternal()

        val transactions = mutableListOf<SaveTransactionModel>()

        val path = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .path + "/" + saveExcelToFirebaseModel.filePath.replaceFirst(
            ".+${Environment.DIRECTORY_DOWNLOADS}/".toRegex(),
            ""
        )

        val fileInputStream = FileInputStream(File(path))

        val workBook = HSSFWorkbook(fileInputStream)

        val sheet: Sheet = workBook.getSheetAt(0)
        val rowIterator: Iterator<Row> = sheet.iterator()

        try {
            rowIterator.next()
            rowIterator.next()
            rowIterator.next()

            while (rowIterator.hasNext()) {
                val row = rowIterator.next()
                val date = row.getCell(2).parseDate()

                val transaction = SaveTransactionModel(
                    accountKey = saveExcelToFirebaseModel.accountKey,
                    yearKey = date.split("/")[2],
                    date = date,
                    name = row.getCell(0).stringCellValue,
                    currency = row.getCell(4).stringCellValue,
                    type = row.getCell(1).stringCellValue,
                    sum = row.getCell(3).numericCellValue,
                ).apply {
                    rateCBR = row.getCell(5).numericCellValue
                    tax = row.getCell(6).numericCellValue
                }
                transactions.add(transaction)
            }

        } catch (_: Exception) {
            //TODO() первые три строчки пропуск
        }
        return transactions
    }

    private fun Cell.parseDate() =
        if (cellType === CellType.NUMERIC) {
            val currentDate = dateCellValue
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
            dateFormat.format(currentDate)
        } else {
            stringCellValue.replace("\\.", "/")
        }
}