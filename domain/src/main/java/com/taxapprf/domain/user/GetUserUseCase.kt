package com.taxapprf.domain.user

import com.taxapprf.domain.ActivityRepository

class GetUserUseCase(
    private val repository: ActivityRepository
) {
    fun execute() = repository.getUser()
}