package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(signInModel: SignInModel) = flow {
        userRepository.signIn(signInModel)

        emit(Unit)
    }
}