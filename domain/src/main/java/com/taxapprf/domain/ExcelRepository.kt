package com.taxapprf.domain

import android.net.Uri
import com.taxapprf.domain.excel.ExportExcelModel
import com.taxapprf.domain.excel.ImportExcelModel
import com.taxapprf.domain.transaction.SaveTransactionModel

interface ExcelRepository {
    suspend fun export(exportExcelModel: ExportExcelModel): Uri?
    suspend fun import(importExcelModel: ImportExcelModel): List<SaveTransactionModel>
}
