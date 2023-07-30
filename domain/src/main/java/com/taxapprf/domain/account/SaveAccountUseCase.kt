package com.taxapprf.domain.account

import com.taxapprf.domain.ActivityRepository
import javax.inject.Inject

class SaveAccountUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    fun execute(accountName: String) = repository.saveAccount(accountName)
}