package com.taxapprf.domain.account

data class AccountsModel(
    val active: AccountModel,
    val inactive: List<AccountModel>
)