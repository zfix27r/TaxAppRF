package com.taxapprf.domain.user

import com.taxapprf.domain.UserRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: UserRepository
) {
    fun execute(userSignUpModel: SignUpModel) = flow {
        repository.signUp(userSignUpModel)

        emit(Unit)
    }
}