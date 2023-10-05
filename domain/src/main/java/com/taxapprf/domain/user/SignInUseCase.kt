package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import com.taxapprf.domain.sync.SyncAllUseCase
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val syncAllUseCase: SyncAllUseCase,
) {
    fun execute(signInModel: SignInModel) = flow {
        userRepository.signIn(signInModel)
        syncAllUseCase.execute()
        emit(Unit)
    }
}