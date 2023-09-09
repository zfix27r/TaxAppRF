package com.taxapprf.data.sync

interface SyncRemote {
    var key: String?
    val syncAt: Long?
}