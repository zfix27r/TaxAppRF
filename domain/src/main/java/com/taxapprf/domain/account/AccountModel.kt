package com.taxapprf.domain.account

import com.taxapprf.domain.Sync


data class AccountModel(
    val id: Int,
    override val key: String,
    val isActive: Boolean,
    override val isSync: Boolean,
    override val isDelete: Boolean,
    override val syncAt: Long,
) : Sync