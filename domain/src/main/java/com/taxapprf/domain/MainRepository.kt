package com.taxapprf.domain

import com.taxapprf.domain.main.SaveTransaction1Model

interface MainRepository {
    suspend fun saveTransaction(saveTransaction1Model: SaveTransaction1Model)
}