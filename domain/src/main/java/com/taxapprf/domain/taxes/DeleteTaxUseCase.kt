package com.taxapprf.domain.taxes

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class DeleteTaxUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(request: FirebaseRequestModel) =
        repository.deleteReport(request)
}