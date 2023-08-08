package com.taxapprf.domain.account

import com.taxapprf.domain.AccountRepository
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    fun execute() = repository.getAccounts()
}