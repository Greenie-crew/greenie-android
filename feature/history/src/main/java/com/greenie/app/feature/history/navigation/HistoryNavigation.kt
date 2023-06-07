package com.greenie.app.feature.history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.greenie.app.feature.history.HistoryRoute

const val historyNavigationRoute = "history_route"

fun NavController.navigateToHistory(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    this.navigate(historyNavigationRoute, navOptions)
}

fun NavGraphBuilder.historyScreen(
    showMessage: (String) -> Unit,
) {
    composable(
        route = historyNavigationRoute,
    ) { _ ->
        HistoryRoute(
            showMessage = showMessage
        )
    }
}