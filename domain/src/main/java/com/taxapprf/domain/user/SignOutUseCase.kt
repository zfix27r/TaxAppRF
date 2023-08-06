package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: UserRepository
) {
    fun execute() = repository.signOut()
}