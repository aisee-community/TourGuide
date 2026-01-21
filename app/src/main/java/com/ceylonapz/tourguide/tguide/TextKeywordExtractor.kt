package com.ceylonapz.tourguide.tguide

object TextKeywordExtractor {

    private val quotedTextRegex = "\"([^\"]+)\"".toRegex()

    fun extract(text: String): String? {
        return quotedTextRegex.find(text)
            ?.groups
            ?.get(1)
            ?.value
            ?.trim()
    }
}