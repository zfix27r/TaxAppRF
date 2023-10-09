package com.taxapprf.data.sync

interface SyncLocal {
    val remoteKey: String?
    val isSync: Boolean
    val syncAt: Long
}