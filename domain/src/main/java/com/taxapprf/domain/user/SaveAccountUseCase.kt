package com.taxapprf.domain.user

import com.taxapprf.domain.ActivityRepository
import javax.inject.Inject

class SaveAccountUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    fun execute(accountModel: AccountModel) =
        repository.saveAccount(accountModel)
}