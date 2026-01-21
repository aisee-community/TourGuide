package com.ceylonapz.tourguide

import android.app.Application
import android.content.Context
import com.ceylonapz.tourguide.tguide.AppController
import com.ceylonapz.tourguide.tguide.StorageHelper
import org.aisee.template_codebase.camera.CameraCore

class AiSeeApp : Application() {

    lateinit var cameraCore: CameraCore
        private set

    lateinit var appController: AppController
        private set

    override fun onCreate() {
        super.onCreate()
        appContext = this

        StorageHelper.init(this)
        cameraCore = CameraCore(this)
        appController = AppController(cameraCore)
    }
}

lateinit var appContext: Context
    private set