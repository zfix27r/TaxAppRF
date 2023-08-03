package com.taxapprf.domain.user

import com.taxapprf.domain.SignRepository

class GetUserUseCase(
    private val repository: SignRepository
) {
    fun execute() = repository.getUser()
}