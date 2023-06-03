package com.greenie.app.feature.deepmedihome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.greenie.app.feature.deepmedihome.DeepmediHomeRoute

const val deepmediHomeNavigationRoute = "deepmedi_home_route"

fun NavController.navigateToDeepmediHome(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    this.navigate(deepmediHomeNavigationRoute, navOptions)
}

fun NavGraphBuilder.deepmediHomeScreen(
    onNavigateToResult: () -> Unit,
    onExit: () -> Unit,
    showMessage: (String) -> Unit,
) {
    composable(
        route = deepmediHomeNavigationRoute,
    ) { _ ->
        DeepmediHomeRoute(
            onNavigateToResult = onNavigateToResult,
            onExit = onExit,
            showMessage = showMessage,
        )
    }
}