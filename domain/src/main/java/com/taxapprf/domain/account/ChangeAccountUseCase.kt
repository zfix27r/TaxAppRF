package com.taxapprf.domain.account

import com.taxapprf.domain.AccountRepository
import javax.inject.Inject

class ChangeAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    fun execute(oldAccountModel: AccountModel, newAccountModel: AccountModel) =
        repository.changeAccount(oldAccountModel, newAccountModel)
}