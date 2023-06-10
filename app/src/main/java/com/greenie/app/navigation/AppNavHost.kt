package com.greenie.app.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.greenie.app.feature.history.navigation.historyScreen
import com.greenie.app.feature.home.navigation.homeScreen
import com.greenie.app.feature.record.navigation.navigateToRecord
import com.greenie.app.feature.record.navigation.recordScreen
import com.greenie.app.feature.result.navigation.navigateToResult
import com.greenie.app.feature.result.navigation.resultScreen
import com.greenie.app.feature.tracking.navigation.navigateToTracking
import com.greenie.app.feature.web.navigation.navigateToWeb
import com.greenie.app.feature.web.navigation.webScreen
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
    val startDestination = TopLevelDestination.Home.route

    val context = LocalContext.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        homeScreen(
            showMessage = { text ->
                snackbarHostState.showMessage(
                    coroutineScope = coroutineScope,
                    text = text,
                )
            },
            onNavigateToRecord = {
                navController.navigateToRecord() {

                }
            },
            onNavigateToTracking = {
                navController.navigateToTracking()
            },
            onNavigateToWeb = { url ->
                navController.navigateToWeb(url) {
                    launchSingleTop = true
                    restoreState = false
                }
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
            onNavigateToResult = { fileName ->
                navController.navigateToResult(fileName) {
                    launchSingleTop = true
                    restoreState = false
                }
            },
        )

        historyScreen(
            showMessage = { text ->
                snackbarHostState.showMessage(
                    coroutineScope = coroutineScope,
                    text = text,
                )
            },
            onNavigateToResult = { fileName ->
                navController.navigateToResult(fileName) {
                    launchSingleTop = true
                    restoreState = false
                }
            },
        )

        resultScreen(
            showMessage = { text ->
                snackbarHostState.showMessage(
                    coroutineScope = coroutineScope,
                    text = text,
                )
            },
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToWeb = { url ->
                navController.navigateToWeb(url) {
                    val previousRoute = navController.previousBackStackEntry?.destination?.route
                    popUpTo(previousRoute ?: startDestination) {
                        inclusive = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            },
        )

        webScreen(
            showMessage = { text ->
                snackbarHostState.showMessage(
                    coroutineScope = coroutineScope,
                    text = text,
                )
            },
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToRecord = {
                navController.navigateToRecord()
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
