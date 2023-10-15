package com.taxapprf.data.sync

interface ISyncLocalModel {
    val remoteKey: String?
    val isSync: Boolean
    val syncAt: Long
}