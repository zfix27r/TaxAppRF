package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import javax.inject.Inject

class IsSignInUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend fun execute() = repository.isSignIn()
}