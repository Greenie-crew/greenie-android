package com.greenie.app.feature.tracking.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.greenie.app.feature.tracking.TrackingRoute

const val trackingNavigationRoute = "tracking_route"

fun NavController.navigateToTracking(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    this.navigate(trackingNavigationRoute, navOptions)
}

fun NavGraphBuilder.trackingScreen(
    showMessage: (String) -> Unit,
    onStartTracking: () -> Unit,
    onPauseTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable(
        route = trackingNavigationRoute,
    ) { _ ->
        TrackingRoute(
            showMessage = showMessage,
            onStartTracking = onStartTracking,
            onPauseTracking = onPauseTracking,
            onStopTracking = onStopTracking,
            onNavigateBack = onNavigateBack,
        )
    }
}