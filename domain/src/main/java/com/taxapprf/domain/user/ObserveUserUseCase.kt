package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository

class ObserveUserUseCase(
    private val repository: UserRepository
) {
    fun execute() = repository.observeUserWithAccounts()
}