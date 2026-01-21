package com.ceylonapz.tourguide.tguide

import android.util.Log
import com.ceylonapz.tourguide.BuildConfig
import com.ceylonapz.tourguide.agent.GeminiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.aisee.template_codebase.camera.CameraCore
import org.aisee.template_codebase.internal_utils.LEDUtils

class AppController(private val cameraCore: CameraCore) {

    private val geminiClient =
        GeminiClient(BuildConfig.GEMINI_API_KEY)

    fun openMLCamera() {

        LEDUtils.setled(LEDUtils.FRONT, true)

        val textUseCase = TextUseCaseFactory.create(cameraCore) { detectedText ->

            val keyword = TextKeywordExtractor.extract(detectedText)

            if (keyword != null) {

                cameraCore.unbindAll()
                LEDUtils.setled(LEDUtils.FRONT, false)

                // Send to Gemini
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val info =
                            geminiClient.getHistoricalInfo(keyword)

                        Log.d(TAG, "Gemini response:\n$info")

                        // 🔜 Next: TTS speak(info)
                    } catch (e: Exception) {
                        Log.e(TAG, "Gemini error", e)
                    }
                }
            }
        }

        cameraCore.bind(listOf(textUseCase))
    }

    companion object {
        private const val TAG = "AiSeeTG"
    }
}