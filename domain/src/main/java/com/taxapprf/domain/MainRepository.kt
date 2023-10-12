package com.taxapprf.domain

import com.taxapprf.domain.main.SaveTransactionModel

interface MainRepository {
    suspend fun saveTransaction(saveTransaction1Model: SaveTransactionModel)
}