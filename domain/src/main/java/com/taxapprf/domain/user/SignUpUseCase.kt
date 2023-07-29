package com.taxapprf.domain.user

import com.taxapprf.domain.ActivityRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    fun execute(userSignUpModel: SignUpModel) =
        repository.signUp(userSignUpModel)
}