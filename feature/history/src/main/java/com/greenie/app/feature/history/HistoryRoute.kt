package com.greenie.app.feature.history

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenie.app.common.audioanalyze.RecordFileManager

@Composable
internal fun HistoryRoute(
    showMessage: (String) -> Unit,
    onNavigateToResult: (fileName: String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyUiState by viewModel.historyUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    
    val mediaPlayer = remember { MediaPlayer() }
    var playingFileName by remember { mutableStateOf("") }
    val errorMessage = stringResource(id = R.string.history_file_error_message)
    LaunchedEffect(playingFileName) {
        mediaPlayer.reset()
        if (playingFileName.isNotEmpty()) {
            mediaPlayer.apply {
                val recordFile = RecordFileManager(context).getRecordFile(playingFileName)
                if (recordFile == null) {
                    showMessage(errorMessage)
                    return@apply
                }
                setDataSource(recordFile.inputStream().fd)
                setOnCompletionListener {
                    playingFileName = ""
                }
                prepare()
                start()
            }
        }
    }

    HistoryScreen(
        historyUiState = historyUiState,
        onClickPlay = { recordHistoryEntity ->
            playingFileName = if (recordHistoryEntity.baseInfo.fileName == playingFileName) {
                ""
            } else {
                recordHistoryEntity.baseInfo.fileName
            }
        },
        onClickShowAnalyze = { recordHistoryEntity ->
            onNavigateToResult(recordHistoryEntity.baseInfo.fileName)
        },
        currentPlayingFileName = playingFileName,
        getHistoryByDate = { year, month ->
            viewModel.getHistoryByDate(year, month)
        }
    )
}