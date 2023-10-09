package com.taxapprf.domain.sync

import com.taxapprf.domain.SyncRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SyncAllUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend fun execute() =
        flow {
            syncRepository.syncAll()
            emit(Unit)
        }
}