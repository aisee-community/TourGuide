package com.ceylonapz.tourguide.agent

import com.ceylonapz.tourguide.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiClient() {

    private val model = GenerativeModel(
        modelName = "gemini-3-pro-preview",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun getHistoricalInfo(keyword: String, currentLanguage: TourLanguage): String =
        withContext(Dispatchers.IO) {

            val prompt = """
                You are a professional tour guide and an audio tour guide.
                
                Explain "$keyword" in ${currentLanguage.geminiName}.
                Use simple, natural spoken language.
                Use short sentences.
                Do not use markdown, symbols, or lists.
                Keep it under 80 words.
            
            """.trimIndent()

            val response = model.generateContent(prompt)

            response.text ?: "No information found."
        }
}