package com.ceylonapz.tourguide.tguide

import android.util.Log
import com.ceylonapz.tourguide.agent.GeminiClient
import com.ceylonapz.tourguide.agent.TourGuideListener
import com.ceylonapz.tourguide.agent.TourLanguage
import com.ceylonapz.tourguide.appContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.aisee.template_codebase.camera.CameraCore
import org.aisee.template_codebase.internal_utils.LEDUtils

class AppController(private val cameraCore: CameraCore) {

    private var currentLanguage: TourLanguage = TourLanguage.ENGLISH

    var listener: TourGuideListener? = null

    private val geminiClient = GeminiClient()

    fun onTestRun() {

        val keyword = "Sri Dalada Maligawa"
        listener?.onKeywordDetected("Scanning...")
        listener?.onKeywordDetected(keyword)
        listener?.onLoading()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val info =
                    geminiClient.getHistoricalInfo(keyword, currentLanguage)

                Log.d(TAG, "Gemini response:\n$info")
                listener?.onResponseReceived(info)
                HapticUtils.one(appContext)

                TTSHelper.callTtsApi(info, true)

            } catch (e: Exception) {
                LEDUtils.setled(LEDUtils.FRONT, false)
                Log.e(TAG, "Gemini error", e)
                listener?.onResponseReceived("Error ${e.toString()}")

                TTSHelper.callTtsApi("Error ${e.message}", true)
            }
        }
    }

    fun openMLCamera() {

        listener?.onKeywordDetected("Scanning...")
        LEDUtils.setled(LEDUtils.FRONT, true)
        HapticUtils.one(appContext)

        val textUseCase = TextUseCaseFactory.create(cameraCore) { detectedText ->

            val keyword = TextKeywordExtractor.extract(detectedText)

            if (keyword != null) {

                cameraCore.unbindAll()
                LEDUtils.setled(LEDUtils.FRONT, false)

                HapticUtils.one(appContext)
                listener?.onKeywordDetected(keyword)
                listener?.onLoading()

                // Send to Gemini
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val info =
                            geminiClient.getHistoricalInfo(keyword, currentLanguage)

                        Log.d(TAG, "Gemini response:\n$info")
                        listener?.onResponseReceived(info)
                        HapticUtils.one(appContext)

                        // TTS speak(info)
                    } catch (e: Exception) {
                        LEDUtils.setled(LEDUtils.FRONT, false)
                        Log.e(TAG, "Gemini error", e)
                        listener?.onResponseReceived("Error ${e.toString()}")
                        HapticUtils.double(appContext)
                    }
                }

                LEDUtils.setled(LEDUtils.FRONT, false)
            }
        }

        cameraCore.bind(listOf(textUseCase))
    }

    fun setLanguage(language: TourLanguage) {
        currentLanguage = language
    }

    companion object {
        private const val TAG = "AiSeeTG"
    }
}