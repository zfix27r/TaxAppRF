package com.taxapprf.domain.report

import com.taxapprf.domain.FirebasePathModel
import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class DeleteReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(firebasePathModel: FirebasePathModel) =
        repository.deleteReport(firebasePathModel)
}