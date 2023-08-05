package com.taxapprf.domain.user

import com.taxapprf.domain.AccountRepository
import javax.inject.Inject

class SaveAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    fun execute(saveAccountModel: SaveAccountModel) =
        repository.saveAccount(saveAccountModel)
}