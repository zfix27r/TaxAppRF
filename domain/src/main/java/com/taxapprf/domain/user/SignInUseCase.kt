package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: UserRepository
) {
    fun execute(userLogInModel: SignInModel) =
        repository.signIn(userLogInModel)
}