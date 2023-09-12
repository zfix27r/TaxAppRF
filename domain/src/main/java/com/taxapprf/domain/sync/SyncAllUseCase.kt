package com.taxapprf.domain.sync

import com.taxapprf.domain.NetworkManager
import com.taxapprf.domain.SyncRepository
import javax.inject.Inject

class SyncAllUseCase @Inject constructor(
    private val networkManager: NetworkManager,
    private val syncRepository: SyncRepository
) {
    fun execute() {
        if (networkManager.available) syncRepository.syncAll()
    }
}