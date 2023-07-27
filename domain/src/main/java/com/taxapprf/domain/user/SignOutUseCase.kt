package com.taxapprf.domain.user

import com.taxapprf.domain.ActivityRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    fun execute() = repository.signOut()
}