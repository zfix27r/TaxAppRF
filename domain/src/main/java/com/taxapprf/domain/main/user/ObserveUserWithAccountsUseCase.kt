package com.taxapprf.domain.main.user

import com.taxapprf.domain.MainRepository

class ObserveUserWithAccountsUseCase(
    private val mainRepository: MainRepository
) {
    fun execute(observeUserWithAccountsModel: ObserveUserWithAccountsModel) =
        mainRepository.observeUserWithAccounts(observeUserWithAccountsModel)
}