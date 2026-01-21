package com.ceylonapz.tourguide.agent

interface TourGuideListener {
    fun onKeywordDetected(keyword: String)
    fun onLoading()
    fun onResponseReceived(response: String)
}