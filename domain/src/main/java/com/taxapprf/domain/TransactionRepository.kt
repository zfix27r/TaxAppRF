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
    fun observeTransactions(observeTransactionsModel: ObserveTransactionsModel): Flow<List<TransactionModel>>
    fun saveTransaction(saveTransactionModel: SaveTransactionModel): Flow<Unit>
    fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel): Flow<Unit>
    fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel): Flow<Uri>
    fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel): Flow<Uri>
    fun saveTransactionsFromExcel(saveTransactionsFromExcelModel: SaveTransactionsFromExcelModel): Flow<Unit>
}