package com.greenie.app.feature.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenie.app.core.designsystem.component.LoadingWheel
import com.greenie.app.core.designsystem.theme.AppTheme
import com.greenie.app.core.model.RecordServiceData
import com.greenie.app.core.model.RecordServiceState

@Composable
internal fun RecordRoute(
    showMessage: (String) -> Unit,
    onStartRecord: () -> Unit,
    onPauseRecord: () -> Unit,
    onSaveRecord: () -> Unit,
    onNavigateToResult: (fileName: String) -> Unit,
    viewModel: RecordViewModel = hiltViewModel()
) {
    val recordUiState by viewModel.recordUiState.collectAsStateWithLifecycle()
    val recordServiceData by viewModel.recordServiceData.collectAsStateWithLifecycle()

    var reserveNavigationToResult by remember { mutableStateOf(false) }

    LaunchedEffect(recordUiState) {
        if (recordUiState is RecordUiState.SAVED && reserveNavigationToResult) {
            reserveNavigationToResult = false
            onNavigateToResult(recordServiceData.fileName)
//            TensorflowHelper(context).analyzeAudio(
//                getRecordFile(context, recordServiceData.fileName)
//            ).collectLatest { result ->
//                Log.e("RecordRoute", "result: $result")
//            }
        }
    }

    RecordScreen(
        recordServiceData = recordServiceData,
        onStartRecord = {
            onStartRecord()
        },
        onPauseRecord = {
            onPauseRecord()
        },
        onSaveRecord = {
            onSaveRecord()
        },
        onAnalyseRecord = {
            reserveNavigationToResult = true
            onSaveRecord()
        },
    )

    if (recordUiState is RecordUiState.LOADING) {
        LoadingWheel()
    }
}

internal const val DecibelPointerSize = 0.16f

@Composable
internal fun RecordScreen(
    recordServiceData: RecordServiceData,
    onStartRecord: () -> Unit,
    onPauseRecord: () -> Unit,
    onSaveRecord: () -> Unit,
    onAnalyseRecord: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RecordMeterSection(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            recordServiceData = recordServiceData,
        )
        Spacer(
            modifier = Modifier
                .weight(0.4f)
        )
        RecordButtonSection(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(0.7f)
                .wrapContentHeight(),
            isRecording = recordServiceData.serviceState == RecordServiceState.RECORDING,
            showAnalyzeButton = recordServiceData.serviceState == RecordServiceState.RECORDING
                    || recordServiceData.serviceState == RecordServiceState.PAUSED,
            onStartRecord = onStartRecord,
            onPauseRecord = onPauseRecord,
            onSaveRecord = onSaveRecord,
            onAnalyseRecord = onAnalyseRecord,
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 500,
    heightDp = 480,
)
@Composable
private fun RecordScreenPreview() {
    AppTheme {
        RecordScreen(
            recordServiceData = RecordServiceData(
                fileName = "Test",
                createdTime = 0L,
                serviceState = RecordServiceState.RECORDING,
                decibelValue = 70.2f,
                minimumDecibel = 0f,
                maximumDecibel = 0f,
                averageDecibel = 0f,
            ),
            onStartRecord = {},
            onPauseRecord = {},
            onSaveRecord = {},
            onAnalyseRecord = {},
        )
    }
}