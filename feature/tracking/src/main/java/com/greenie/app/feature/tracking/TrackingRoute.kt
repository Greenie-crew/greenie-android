package com.greenie.app.feature.tracking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenie.app.core.model.TrackingServiceState
import com.greenie.app.feature.tracking.component.TrackingResultDialog
import com.greenie.app.feature.tracking.component.TrackingRunningDialog

@Composable
internal fun TrackingRoute(
    showMessage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onStartTracking: () -> Unit,
    onPauseTracking: () -> Unit,
    onStopTracking: () -> Unit,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val trackingUiState by viewModel.trackingUiState.collectAsStateWithLifecycle()

    TrackingAgreementScreen(
        showMessage = showMessage,
        onClickTrackingStart = onStartTracking,
    )

    when (trackingUiState) {
        is TrackingUiState.Idle -> {

        }
        is TrackingUiState.Running -> {
            val trackingData = (trackingUiState as TrackingUiState.Running).trackingServiceData
            TrackingRunningDialog(
                onClickStart = onStartTracking,
                onClickPause = onPauseTracking,
                onClickStop = onStopTracking,
                timeText = trackingData.leftTime.let{ leftTime ->
                    String.format(
                        "%02d:%02d:%02d",
                        leftTime / 1000 / 60 / 60,
                        leftTime / 1000 / 60 % 60,
                        leftTime / 1000 % 60
                    )
                },
                onDismiss = onNavigateBack,
                isRunning = trackingData.serviceState == TrackingServiceState.TRACKING,
            )
        }
        is TrackingUiState.End -> {
            val trackingData = (trackingUiState as TrackingUiState.End).trackingServiceData
            TrackingResultDialog(
                onDismiss = onNavigateBack,
                onTrackingAgain = onStartTracking,
                analyzeData = trackingData.loudNoiseHistory,
            )
        }
    }
}