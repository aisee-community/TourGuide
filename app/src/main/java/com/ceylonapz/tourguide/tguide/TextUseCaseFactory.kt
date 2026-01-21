package com.ceylonapz.tourguide.tguide

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCase
import org.aisee.template_codebase.camera.CameraCore

object TextUseCaseFactory {

    fun create(
        cameraCore: CameraCore,
        onTextDetected: (String) -> Unit
    ): UseCase {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(
                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            )
            .build()
            .apply {
                setAnalyzer(
                    cameraCore.getExecutor(),
                    TextAnalyzer(onTextDetected)
                )
            }
    }
}