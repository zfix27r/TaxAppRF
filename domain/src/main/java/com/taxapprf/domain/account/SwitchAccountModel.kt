package com.taxapprf.domain.account


data class SwitchAccountModel(
    val passiveAccountKey: String,
    val activeAccountKey: String
)