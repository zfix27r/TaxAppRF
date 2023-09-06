package com.taxapprf.domain

import android.net.Uri
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.ObserveTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observe(observeTransactionsModel: ObserveTransactionsModel): Flow<List<TransactionModel>>
    fun save(saveTransactionModel: SaveTransactionModel): Flow<Unit>
    fun delete(deleteTransactionModel: DeleteTransactionModel): Flow<Unit>
    fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel): Flow<Uri>
    fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel): Flow<Uri>
    fun saveFromExcel(saveTransactionsFromExcelModel: SaveTransactionsFromExcelModel): Flow<Unit>
}