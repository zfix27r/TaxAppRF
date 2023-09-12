package com.taxapprf.domain.account

import com.taxapprf.domain.AccountRepository
import javax.inject.Inject

class ObserveAccountsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    fun execute() = repository.observeAccounts()
}