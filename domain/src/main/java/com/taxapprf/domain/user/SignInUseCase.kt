package com.taxapprf.domain.user

import com.taxapprf.domain.SignRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: SignRepository
) {
    fun execute(userLogInModel: SignInModel) =
        repository.signIn(userLogInModel)
}