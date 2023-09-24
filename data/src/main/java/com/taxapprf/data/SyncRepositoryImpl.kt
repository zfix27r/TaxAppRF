package com.taxapprf.data

import com.taxapprf.domain.NetworkManager
import com.taxapprf.domain.SyncRepository
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager,
) : SyncRepository {
    override fun syncAll() {
        if (networkManager.available) {
        }
    }
}