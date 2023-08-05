package com.taxapprf.domain.user

import com.taxapprf.domain.SignRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: SignRepository
) {
    fun execute() = repository.signOut()
}