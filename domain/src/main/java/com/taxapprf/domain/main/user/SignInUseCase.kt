package com.taxapprf.domain.main.user

import com.taxapprf.domain.MainRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend fun execute(signInModel: SignInModel) =
        mainRepository.signIn(signInModel)
}