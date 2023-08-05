package com.taxapprf.domain.user

import com.taxapprf.domain.SignRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: SignRepository
) {
    fun execute(userSignUpModel: SignUpModel) =
        repository.signUp(userSignUpModel)
}