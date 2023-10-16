package com.taxapprf.data.sync

interface ISyncRemoteModel {
    var key: String?
    val syncAt: Long?
}