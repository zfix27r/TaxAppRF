package com.taxapprf.domain

interface SyncRepository {
     suspend fun syncAll()
}