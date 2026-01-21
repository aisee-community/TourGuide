package com.ceylonapz.tourguide.tguide

import android.util.Log
import org.aisee.template_codebase.camera.CameraCore
import org.aisee.template_codebase.internal_utils.LEDUtils

class AppController(private val cameraCore: CameraCore) {

    fun openMLCamera() {
        Log.d(TAG, "Camera opened")
        LEDUtils.setled(LEDUtils.FRONT, true)
        LEDUtils.setled(LEDUtils.FRONT, false)
    }

    companion object {
        private const val TAG = "AiSeeTG"
    }
}