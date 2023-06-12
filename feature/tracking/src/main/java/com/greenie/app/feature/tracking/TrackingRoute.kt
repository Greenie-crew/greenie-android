package com.greenie.app.feature.tracking

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenie.app.core.domain.entities.RecordHistoryEntity
import com.greenie.app.core.model.NoiseHistoryData
import com.greenie.app.core.model.TrackingServiceState
import com.greenie.app.feature.tracking.component.TrackingResultDialog
import com.greenie.app.feature.tracking.component.TrackingRunningDialog
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entriesOf
import java.util.Calendar
import java.util.Date

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
                analyzeData = trackingData.loudNoiseHistory,
            )
        }
    }
}