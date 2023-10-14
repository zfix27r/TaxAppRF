package com.taxapprf.domain.main.account

import com.taxapprf.domain.MainRepository
import javax.inject.Inject

class SwitchAccountUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend fun execute(switchAccountModel: SwitchAccountModel) =
        mainRepository.switchAccount(switchAccountModel)
}