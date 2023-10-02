package com.taxapprf.data.sync

interface SyncLocal {
    val remoteKey: String?
    val syncAt: Long
}