package com.ceylonapz.tourguide.agent


data class TourGuideUiState(
    val detectedKeyword: String? = null,
    val isLoading: Boolean = false,
    val responseText: String? = null,
    val selectedLanguage: TourLanguage = TourLanguage.ENGLISH
)

enum class TourLanguage(
    val label: String,
    val geminiName: String
) {
    ENGLISH("English", "English"),
    GERMAN("Deutsch", "German"),
    SINHALA("සිංහල", "Sinhala"),
    CHINESE("中文", "Simplified Chinese"),
}