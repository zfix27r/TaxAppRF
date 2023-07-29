package com.taxapprf.domain.user

import com.taxapprf.domain.ActivityRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    fun execute(userLogInModel: SignInModel) =
        repository.signIn(userLogInModel)
}