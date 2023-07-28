package com.taxapprf.data

import com.taxapprf.domain.TaxesRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TaxesRepositoryImpl @Inject constructor(
    private val firebaseAPI: FirebaseAPI
) : TaxesRepository {
    override fun getTaxes() = flow {
        emit(firebaseAPI.getTaxes())
    }
}