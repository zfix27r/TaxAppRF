package com.taxapprf.domain

interface Sync {
    val key: String
    val isSync: Boolean
    val isDeferredDelete: Boolean
    val syncAt: Long
}