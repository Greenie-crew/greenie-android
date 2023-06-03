package com.greenie.app.navigation

import android.app.Activity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.greenie.app.feature.history.navigation.historyScreen
import com.greenie.app.feature.menu.navigation.menuScreen
import com.greenie.app.feature.record.navigation.recordScreen
import com.greenie.app.service.service.RecordForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun GreenieNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    val coroutineScope = rememberCoroutineScope()
    val startDestination = TopLevelDestination.Menu.route

    val activity = LocalContext.current as Activity
    val context = LocalContext.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        menuScreen (
            showMessage = { text ->
                snackbarHostState.showMessage(
                    coroutineScope = coroutineScope,
                    text = text,
                )
            },
        )

        recordScreen(
            showMessage = { text ->
                snackbarHostState.showMessage(
                    coroutineScope = coroutineScope,
                    text = text,
                )
            },
            onStartRecord = {
                RecordForegroundService.startRecordService(context)
            },
            onPauseRecord = {
                RecordForegroundService.pauseRecordService(context)
            },
            onSaveRecord = {
                RecordForegroundService.saveRecord(context)
            },
            onAnalyseRecord = {
                RecordForegroundService.analyzeRecord(context)
            },
        )

        historyScreen(
            onNavigateToHome = {
                navController.popBackStack(startDestination, inclusive = true)
            },
            showMessage = { text ->
                snackbarHostState.showMessage(
                    coroutineScope = coroutineScope,
                    text = text,
                )
            },
        )
    }
}

private fun SnackbarHostState.showMessage(
    coroutineScope: CoroutineScope,
    text: String,
) {
    coroutineScope.launch {
        currentSnackbarData?.dismiss()
        showSnackbar(text)
    }
}
