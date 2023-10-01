package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute(signInModel: SignInModel) {
        userRepository.signIn(signInModel)
        /*
        * TODO объемная задача по объеденению локальных данных и удаленных
        *  необходимо выработать стратегию объединения
        * */
    }
}