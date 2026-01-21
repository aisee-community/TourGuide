package com.ceylonapz.tourguide

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.ceylonapz.tourguide.tguide.AppController

@SuppressLint("AccessibilityPolicy")
class AiSeeAccessibilityService : AccessibilityService() {

    private lateinit var appCont: AppController

    override fun onServiceConnected() {
        Log.d(TAG, "Accessibility Service Connected")

        val app = application as AiSeeApp
        appCont = app.appController
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP) {
            val keyCodeName = KeyEvent.keyCodeToString(event.keyCode)
            Log.d(TAG, "Physical Button Pressed: $keyCodeName")

            when (event.keyCode) {
                KeyEvent.KEYCODE_F2 -> {
                    appCont.openMLCamera()
                    return true
                }
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

    companion object {
        private const val TAG = "MyAccessibilityService"
    }
}