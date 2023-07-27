package com.taxapprf.domain.user

import com.taxapprf.domain.ActivityRepository
import javax.inject.Inject


class IsSignInUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    fun execute() = repository.isSignIn()
}