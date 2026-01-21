package com.ceylonapz.tourguide

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ceylonapz.tourguide.ui.theme.TourGuideTheme
import org.aisee.template_codebase.internal_utils.AccessibilityHelper.Companion.enableAccessibilityService
import org.aisee.template_codebase.internal_utils.AccessibilityHelper.Companion.isAccessibilityServiceEnabled

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "AiSeeTG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TourGuideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Enable Button Service
        enableAccessibilityService(this)

        if (isAccessibilityServiceEnabled(this, AiSeeAccessibilityService::class.java)) {
            Log.d(TAG, "Accessibility service is enabled.")
        } else {
            Log.d(TAG, "Accessibility service not enabled even after auto-enable attempt.")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TourGuideTheme {
        Greeting("Android")
    }
}