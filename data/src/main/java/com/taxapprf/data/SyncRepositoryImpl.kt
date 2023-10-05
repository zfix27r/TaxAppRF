package com.taxapprf.data

import com.taxapprf.data.error.DataErrorConnection
import com.taxapprf.domain.SyncRepository
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager,
) : SyncRepository {
    override suspend fun syncAll() {
        if (networkManager.available) {
            //syncAccounts.sync()

        } else throw DataErrorConnection()
    }
}