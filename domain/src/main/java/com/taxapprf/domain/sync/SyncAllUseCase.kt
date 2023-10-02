package com.taxapprf.domain.sync

import com.taxapprf.domain.SyncRepository
import javax.inject.Inject

class SyncAllUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    fun execute() = syncRepository.syncAll()
}