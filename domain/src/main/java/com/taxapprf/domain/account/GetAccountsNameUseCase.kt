package com.taxapprf.domain.account

import com.taxapprf.domain.ActivityRepository
import javax.inject.Inject


class GetAccountsNameUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    fun execute() = repository.getAccounts()
}