package com.taxapprf.domain.user

data class UserWithAccountsModel(
    val user: UserModel?,
    val activeAccount: AccountModel?,
    val otherAccounts: List<AccountModel>,
)