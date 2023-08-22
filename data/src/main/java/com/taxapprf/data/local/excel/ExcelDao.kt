package com.taxapprf.data.local.excel

import android.net.Uri
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.SaveTransactionModel

interface ExcelDao {
    suspend fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel): Uri
    suspend fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel): Uri
    suspend fun saveExcel(saveExcelToFirebaseModel: SaveTransactionsFromExcelModel): List<SaveTransactionModel>
}