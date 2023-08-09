package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: UserRepository
) {
    fun execute(userSignUpModel: SignUpModel) =
        repository.signUp(userSignUpModel)
}