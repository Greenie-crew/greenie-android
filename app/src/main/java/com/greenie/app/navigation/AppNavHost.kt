package com.greenie.app.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import com.greenie.app.core.domain.usecase.service.ServiceState
import com.greenie.app.feature.history.navigation.historyScreen
import com.greenie.app.feature.home.navigation.homeScreen
import com.greenie.app.feature.record.navigation.recordScreen
import com.greenie.app.feature.result.navigation.navigateToResult
import com.greenie.app.feature.result.navigation.resultScreen
import com.greenie.app.feature.tracking.navigation.trackingScreen
import com.greenie.app.feature.web.navigation.navigateToWeb
import com.greenie.app.feature.web.navigation.webScreen
import com.greenie.app.service.service.RecordForegroundService
import com.greenie.app.service.service.TrackingForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun GreenieNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    serviceState: ServiceState,
    onNavigateToRecord: (NavOptionsBuilder.() -> Unit) -> Boolean,
    onNavigateToTracking: (NavOptionsBuilder.() -> Unit) -> Boolean,
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
                    message = text,
                )
            },
            serviceState = serviceState,
            onNavigateToRecord = {
                onNavigateToRecord {}
            },
            onNavigateToTracking = {
                onNavigateToTracking {}
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
                    message = text,
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

        trackingScreen(
            showMessage = { text ->
                snackbarHostState.showMessage(
                    coroutineScope = coroutineScope,
                    message = text,
                )
            },
            onStartTracking = {
                TrackingForegroundService.startTrackingService(context)
            },
            onPauseTracking = {
                TrackingForegroundService.pauseTrackingService(context)
            },
            onStopTracking = {
                TrackingForegroundService.stopTrackingService(context)
            },
            onNavigateBack = {
                navController.popBackStack()
            },
        )

        historyScreen(
            showMessage = { text ->
                snackbarHostState.showMessage(
                    coroutineScope = coroutineScope,
                    message = text,
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
                    message = text,
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
                    message = text,
                )
            },
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToRecord = {
                onNavigateToRecord {}
            }
        )
    }
}

internal fun SnackbarHostState.showMessage(
    coroutineScope: CoroutineScope,
    message: String,
) {
    coroutineScope.launch {
        currentSnackbarData?.dismiss()
        showSnackbar(message)
    }
}
