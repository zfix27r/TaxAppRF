package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import javax.inject.Inject

class SwitchAccountUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend fun execute(switchAccountModel: SwitchAccountModel) =
        repository.switchAccount(switchAccountModel)
}