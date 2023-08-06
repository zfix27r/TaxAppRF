package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository

class GetUserUseCase(
    private val repository: UserRepository
) {
    fun execute() = repository.getUser()
}