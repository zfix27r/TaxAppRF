package com.taxapprf.domain.main.user

import com.taxapprf.domain.MainRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend fun execute(userSignUpModel: SignUpModel) =
        mainRepository.signUp(userSignUpModel)
}