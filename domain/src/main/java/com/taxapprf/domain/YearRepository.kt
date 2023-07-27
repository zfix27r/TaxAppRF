package com.taxapprf.domain

import com.taxapprf.domain.year.GetYearModel
import kotlinx.coroutines.flow.Flow

interface YearRepository {
    fun getYears(firebaseModel: FirebaseRequestModel): Flow<List<GetYearModel>>
}