package com.ceylonapz.tourguide

import android.app.Application
import android.content.Context

class AiSeeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}

lateinit var appContext: Context
    private set