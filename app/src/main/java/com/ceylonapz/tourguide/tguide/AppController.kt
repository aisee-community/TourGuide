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

            val keyword = TextKeywordExtractor.extract(detectedText)

            if (keyword != null) {
                Log.d(TAG, "KEYWORD FOUND: $keyword")

                cameraCore.unbindAll()
                LEDUtils.setled(LEDUtils.FRONT, false)

                // 🔜 Next step: send `keyword` to Gemini
            } else {
                Log.d(TAG, "No quoted keyword found")
            }
        }

        cameraCore.bind(listOf(textUseCase))
    }

    companion object {
        private const val TAG = "AiSeeTG"
    }
}