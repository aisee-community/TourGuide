package com.ceylonapz.tourguide.tguide

import android.util.Log
import org.aisee.template_codebase.camera.CameraCore
import org.aisee.template_codebase.internal_utils.LEDUtils

class AppController(private val cameraCore: CameraCore) {

    fun openMLCamera() {
        Log.d(TAG, "ML Camera opened")

        // LED ON while scanning
        LEDUtils.setled(LEDUtils.FRONT, true)

        val textUseCase = TextUseCaseFactory.create(cameraCore) { detectedText ->
            Log.d(TAG, "TEXT FOUND: $detectedText")

            // Stop camera after detection (initial version)
            cameraCore.unbindAll()

            // LED OFF
            LEDUtils.setled(LEDUtils.FRONT, false)

            // Next phase: send detectedText to Gemini
        }

        cameraCore.bind(listOf(textUseCase))
    }

    companion object {
        private const val TAG = "AiSeeTG"
    }
}