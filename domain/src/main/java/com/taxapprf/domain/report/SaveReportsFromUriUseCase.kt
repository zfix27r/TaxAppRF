package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class SaveReportsFromUriUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(saveReportsFromUriModel: SaveReportsFromUriModel) =
        repository.saveReportsFromUri(saveReportsFromUriModel)
}