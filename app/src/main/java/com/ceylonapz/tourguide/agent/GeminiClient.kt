package com.ceylonapz.tourguide.agent

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiClient(apiKey: String) {

    private val language = "Sinhala"
    private val model = GenerativeModel(
        modelName = "gemini-3-pro-preview",
        apiKey = apiKey
    )

    suspend fun getHistoricalInfo(keyword: String): String =
        withContext(Dispatchers.IO) {

            val prompt = """
                You are a professional tour guide and an audio tour guide.
                Explain the historical significance of "$keyword"
                in $language. Use simple spoken $language. a concise and 
                visitor-friendly way in short sentences. 
                Do NOT use markdown, headings, bullet points, or symbols.
                Use simple, spoken English. Keep it under 100 words.
            """.trimIndent()

            val response = model.generateContent(prompt)

            response.text ?: "No information found."
        }
}