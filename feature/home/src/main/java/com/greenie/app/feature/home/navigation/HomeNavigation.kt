package com.greenie.app.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.greenie.app.core.domain.usecase.service.ServiceState
import com.greenie.app.feature.home.HomeRoute

const val homeNavigationRoute = "home_route"

fun NavController.navigateToHome(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    this.navigate(homeNavigationRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    showMessage: (String) -> Unit,
    serviceState: ServiceState,
    onNavigateToRecord: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToWeb: (String) -> Unit,
) {
    composable(
        route = homeNavigationRoute,
    ) { _ ->
        HomeRoute(
            showMessage = showMessage,
            serviceState = serviceState,
            onNavigateToRecord = onNavigateToRecord,
            onNavigateToTracking = onNavigateToTracking,
            onNavigateToWeb = onNavigateToWeb,
        )
    }
}