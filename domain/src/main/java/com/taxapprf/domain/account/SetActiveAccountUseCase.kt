package com.taxapprf.domain.account

import com.taxapprf.domain.ActivityRepository
import javax.inject.Inject


class SetActiveAccountUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    fun execute(accountName: String) = repository.setActiveAccount(accountName)
}