package com.taxapprf.data.sync

interface SyncLocal {
    val key: String
    val isSync: Boolean
    val isDelete: Boolean
    val syncAt: Long
}