package com.taxapprf.domain.user

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.UserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend fun execute() {
        userRepository.deleteAll()
        reportRepository.deleteAll()
        transactionRepository.deleteAll()
        userRepository.signOut()
    }
}