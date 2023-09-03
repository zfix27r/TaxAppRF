package com.taxapprf.domain.account

import com.taxapprf.domain.Sync

data class AccountModel(
    override val key: String,
    val isActive: Boolean,
    override val isSync: Boolean,
    override val syncAt: Long,
) : Sync