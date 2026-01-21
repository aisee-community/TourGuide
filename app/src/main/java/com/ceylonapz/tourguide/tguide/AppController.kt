package com.ceylonapz.tourguide.tguide

import android.util.Log
import com.ceylonapz.tourguide.BuildConfig
import com.ceylonapz.tourguide.agent.GeminiClient
import com.ceylonapz.tourguide.agent.TourGuideListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.aisee.template_codebase.camera.CameraCore
import org.aisee.template_codebase.internal_utils.LEDUtils

class AppController(private val cameraCore: CameraCore) {

    var listener: TourGuideListener? = null

    private val geminiClient =
        GeminiClient(BuildConfig.GEMINI_API_KEY)

    fun openMLCamera() {

        listener?.onKeywordDetected("Scanning...")

        LEDUtils.setled(LEDUtils.FRONT, true)

        val textUseCase = TextUseCaseFactory.create(cameraCore) { detectedText ->

            val keyword = TextKeywordExtractor.extract(detectedText)

            if (keyword != null) {

                cameraCore.unbindAll()
                LEDUtils.setled(LEDUtils.FRONT, false)

                listener?.onKeywordDetected(keyword)
                listener?.onLoading()

                // Send to Gemini
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val info =
                            geminiClient.getHistoricalInfo(keyword)

                        Log.d(TAG, "Gemini response:\n$info")
                        listener?.onResponseReceived(info)

                        // TTS speak(info)
                    } catch (e: Exception) {
                        LEDUtils.setled(LEDUtils.FRONT, false)
                        Log.e(TAG, "Gemini error", e)
                        listener?.onResponseReceived("Error ${e.toString()}")
                    }
                }

                LEDUtils.setled(LEDUtils.FRONT, false)
            }
        }

        cameraCore.bind(listOf(textUseCase))
    }

    companion object {
        private const val TAG = "AiSeeTG"
    }
}