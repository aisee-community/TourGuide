package com.ceylonapz.tourguide

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import org.aisee.template_codebase.camera.CameraCore

@SuppressLint("AccessibilityPolicy")
class AiSeeAccessibilityService : AccessibilityService() {

    private lateinit var cameraCore: CameraCore

    override fun onServiceConnected() {
        Log.d(TAG, "Accessibility Service Connected")
        cameraCore = CameraCore(appContext)
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP) {
            val keyCodeName = KeyEvent.keyCodeToString(event.keyCode)
            Log.d(TAG, "Physical Button Pressed: $keyCodeName")

            when (event.keyCode) {
                // TODO: Implement Button Logic Here
            }
        }
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Use this to intercept UI events
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Accessibility Service Unbound")
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted")
    }

    override fun onDestroy() {
        cameraCore.onDestroy()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MyAccessibilityService"
    }
}