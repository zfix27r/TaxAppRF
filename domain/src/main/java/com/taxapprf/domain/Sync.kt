package com.taxapprf.domain

interface Sync {
    val key: String
    val isSync: Boolean
    val isDelete: Boolean
    val syncAt: Long
}