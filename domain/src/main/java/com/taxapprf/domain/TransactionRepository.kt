package com.taxapprf.domain

import android.net.Uri
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(getTransactionModel: GetTransactionsModel): Flow<List<TransactionModel>>
    fun saveTransactionModel(saveTransactionModel: SaveTransactionModel): Flow<Unit>
    fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel): Flow<Unit>
    fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel): Flow<Uri>
    fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel): Flow<Uri>
}