package com.ceylonapz.tourguide.agent


data class TourGuideUiState(
    val detectedKeyword: String? = null,
    val isLoading: Boolean = false,
    val responseText: String? = null
)
