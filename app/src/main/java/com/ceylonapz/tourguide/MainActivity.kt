package com.ceylonapz.tourguide

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ceylonapz.tourguide.agent.TourGuideListener
import com.ceylonapz.tourguide.agent.TourGuideUiState
import com.ceylonapz.tourguide.ui.theme.TourGuideTheme
import org.aisee.template_codebase.internal_utils.AccessibilityHelper.Companion.enableAccessibilityService
import org.aisee.template_codebase.internal_utils.AccessibilityHelper.Companion.isAccessibilityServiceEnabled

class MainActivity : ComponentActivity(), TourGuideListener {

    companion object {
        private const val TAG = "AiSeeTG"
    }

    private val uiState = mutableStateOf(TourGuideUiState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AiSeeApp
        app.appController.listener = this

        setContent {
            TourGuideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TourGuideScreen(uiState.value, modifier = Modifier.padding(innerPadding))
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

    override fun onKeywordDetected(keyword: String) {
        uiState.value = uiState.value.copy(
            detectedKeyword = keyword,
            responseText = null
        )
    }

    override fun onLoading() {
        uiState.value = uiState.value.copy(isLoading = true)
    }

    override fun onResponseReceived(response: String) {
        uiState.value = uiState.value.copy(
            isLoading = false,
            responseText = response
        )
    }
}

@Composable
fun TourGuideScreen(state: TourGuideUiState, modifier: Modifier) {

    if (
        state.detectedKeyword == null &&
        !state.isLoading &&
        state.responseText == null
    ) {
        IdleTourGuideView()
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "AI Tour Guide",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        state.detectedKeyword?.let {
            Text(
                text = "Detected: \"$it\"",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(12.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
            Text("Fetching historical information…")
        }

        state.responseText?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun IdleTourGuideView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "AI TourGuide",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "Discover history with AI",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Press the play button on your headset to scan and hear the story",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 320.dp)
                )
            }
        }
    }
}