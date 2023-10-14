package com.taxapprf.domain.main.user

import com.taxapprf.domain.MainRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend fun execute() =
        mainRepository.signOut()
}