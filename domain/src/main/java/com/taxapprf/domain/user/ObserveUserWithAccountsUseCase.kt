package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository

class ObserveUserWithAccountsUseCase(
    private val repository: UserRepository
) {
    fun execute(observeUserWithAccountsModel: ObserveUserWithAccountsModel) =
        repository.observeUserWithAccounts(observeUserWithAccountsModel)
}