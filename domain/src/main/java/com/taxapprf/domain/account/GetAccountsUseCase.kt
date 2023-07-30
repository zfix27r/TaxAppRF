package com.taxapprf.domain.account

import com.taxapprf.domain.ActivityRepository
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    fun execute() = repository.getAccounts()
}