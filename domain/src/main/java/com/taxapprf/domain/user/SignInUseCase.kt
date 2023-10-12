package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend fun execute(signInModel: SignInModel) =
        userRepository.signIn(signInModel)
}