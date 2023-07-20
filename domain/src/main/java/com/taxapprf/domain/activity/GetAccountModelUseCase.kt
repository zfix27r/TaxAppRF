package com.taxapprf.domain.activity

import com.taxapprf.domain.ActivityRepository


class GetAccountModelUseCase(
    private val repository: ActivityRepository
) {
    fun execute() = repository.getAccountModel()
}