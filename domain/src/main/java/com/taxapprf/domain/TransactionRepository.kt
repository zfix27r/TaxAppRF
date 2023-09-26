package com.taxapprf.domain

import android.net.Uri
import com.taxapprf.domain.tax.UpdateTaxModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeAll(accountId: Int, reportId: Int): Flow<List<TransactionModel>>
    suspend fun save(saveTransactionModel: SaveTransactionModel): Long?
    suspend fun deleteTransactionStep1(deleteTransactionModel: DeleteTransactionModel): DeleteTransactionModel?
    suspend fun deleteAll(accountId: Int, reportId: Int): Int
    suspend fun updateTax(updateTaxModel: UpdateTaxModel)
    fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel): Flow<Uri>
    fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel): Flow<Uri>
    fun saveFromExcel(saveTransactionsFromExcelModel: SaveTransactionsFromExcelModel): Flow<Unit>
}