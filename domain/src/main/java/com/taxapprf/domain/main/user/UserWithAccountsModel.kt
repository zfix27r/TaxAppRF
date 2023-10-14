package com.taxapprf.domain.main.user

import com.taxapprf.domain.main.account.AccountModel

data class UserWithAccountsModel(
    val user: UserModel?,
    val activeAccount: AccountModel?,
    val otherAccounts: List<AccountModel>,
)