package com.taxapprf.domain.main.analytics

import com.taxapprf.domain.MainRepository

class StartFirebaseAnalyticsUseCase(
    private val mainRepository: MainRepository
) {
    fun execute() = mainRepository.startFirebaseAnalytics()
}