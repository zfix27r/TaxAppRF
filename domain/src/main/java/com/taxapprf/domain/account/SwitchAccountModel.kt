package com.taxapprf.domain.account


data class SwitchAccountModel(
    val inactiveAccountId: Int,
    val inactiveAccountKey: String,
    val activeAccountId: Int? = null,
    val activeAccountKey: String
)